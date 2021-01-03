package com.victoriya.tortube.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.victoriya.tortube.R;
import com.victoriya.tortube.torrentstreamserver.TorrentServerListener;
import com.victoriya.tortube.torrentstreamserver.TorrentStreamServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.victoriya.tortube.ui.main.MainActivity.MAGNET_LINK;

public class StreamingHandler implements TorrentServerListener {
    private static final String TAG=StreamingService.class.getSimpleName();

    private TorrentStreamServer torrentStreamServer;
    private SharedPreferences pref;
    private Application application;
    private static StreamingHandler instance;
//    private UpdateShareViewModel shareViewModel;


    public MutableLiveData<Torrent> torrentFile;
    public MutableLiveData<StreamStatus> progressStatus;
    public MutableLiveData<String> streamingUrl;
    public MutableLiveData<String> state;
    public MutableLiveData<String> error;


    public boolean isServiceActive=false;



    private StreamingHandler(Application application){
        this.application = application;
        pref=PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        if(torrentStreamServer==null){
            initServer();
        }
        torrentFile =new MutableLiveData<>();
        progressStatus =new MutableLiveData<>();
        streamingUrl=new MutableLiveData<>();
        state =new MutableLiveData<>();
        error=new MutableLiveData<>();
    }


    public static StreamingHandler getInstance(Application application){
        if(instance==null){
            instance=new StreamingHandler(application);
        }
        return instance;
    }

    public static StreamingHandler getInstance(){
        return instance;
    }


    public void onHandleIntent(Intent intent) {
        Log.d(TAG,Thread.currentThread().getName().toString());
        if(intent!=null){
            /*shareViewModel=new ViewModelProvider((MainActivity)activity).get(UpdateShareViewModel.class);
            Log.d(TAG,"check"+shareViewModel.hashCode());*/
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        isServiceActive=true;
        String magnetLink=intent.getStringExtra(MAGNET_LINK);
        startStreaming(magnetLink);
    }

    private void startStreaming(String magnetLink) {

        if(torrentStreamServer==null){
            initServer();
        }
        if(torrentStreamServer.isStreaming()){
            torrentStreamServer.stopStream();
            Log.d(TAG,"streaming stop");
        }
        try {
            Log.d(TAG,magnetLink.toString());
            torrentStreamServer.startStream(magnetLink);
            Log.d(TAG,"startStreaming");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initServer() {

        long bufferSize=pref.getLong(application.getString(R.string.buffer_size),1L);
        if(bufferSize > 5){
            bufferSize=5;
        }

        int streamingSpeed=pref.getInt(application.getString(R.string.streaming_speed),0);
        int seedingSpeed=pref.getInt(application.getString(R.string.seeding_speed),0);
        boolean saveStatus=pref.getBoolean(application.getString(R.string.save_status),true);
        if(saveStatus){
            saveStatus=false;
        }
        else {
            saveStatus=true;
        }

        Log.d(TAG,"Buffer size: "+bufferSize+
                " Streaming Speed "+streamingSpeed+
                " Seeding Speed "+seedingSpeed+
                " Save Status "+saveStatus);


        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(saveStatus)
                .prepareSize(bufferSize*1024L*1024L)
                .maxDownloadSpeed(streamingSpeed)
                .maxUploadSpeed(seedingSpeed)
                .build();

        String ipAddress = "127.0.0.1";
        try {
            InetAddress inetAddress = getIpAddress(application.getApplicationContext());
            if (inetAddress != null) {
                ipAddress = inetAddress.getHostAddress();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        int portNumber=pref.getInt(application.getString(R.string.port_number),2000);
        Log.d(TAG,"Port Number "+portNumber);


        torrentStreamServer = TorrentStreamServer.getInstance();
        torrentStreamServer.setTorrentOptions(torrentOptions);
        torrentStreamServer.setServerHost(ipAddress);
        torrentStreamServer.setServerPort(portNumber);
        torrentStreamServer.startTorrentStream();
        torrentStreamServer.addListener(this);
    }



    public static InetAddress getIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        if (ip == 0) {
            return null;
        } else {
            byte[] ipAddress = convertIpAddress(ip);
            return InetAddress.getByAddress(ipAddress);
        }
    }

    private static byte[] convertIpAddress(int ip) {
        return new byte[]{
                (byte) (ip & 0xFF),
                (byte) ((ip >> 8) & 0xFF),
                (byte) ((ip >> 16) & 0xFF),
                (byte) ((ip >> 24) & 0xFF)};
    }

    @Override
    public void onServerReady(String url) {
        setStreamingUrl(url);
        Log.d(TAG, "onServerReady: " + url);
        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setDataAndType(Uri.parse(url), "video/mp4");
        activity.startActivity(intent);*/
    }

    @Override
    public void onStreamPrepared(Torrent torrent) {
        setState("onStreamPrepared");
        Log.d(TAG,"onStreamPrepared"+"-"+String.valueOf(torrent.getPiecesToPrepare()));
    }

    @Override
    public void onStreamStarted(Torrent torrent) {
        setTorrentFile(torrent);
        Log.d(TAG, "isValid"+String.valueOf(torrent.getTorrentHandle().isValid()));
        Log.d(TAG,"onTorrentStarted"+String.valueOf(torrent.getFileNames())+Thread.currentThread().getName());
    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {
        e.printStackTrace();
        setError(e.getLocalizedMessage());
        e.printStackTrace();
        cleanHandler();
    }

    @Override
    public void onStreamReady(Torrent torrent) {
        Log.d(TAG, "onStreamReady"+String.valueOf(torrent.getFileNames()));
    }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus status) {
        setProgressStatus(status);
        Log.d(TAG,"progress-"+status.bufferProgress);
    }

    @Override
    public void onStreamStopped() {
        Log.d(TAG,"onStreamStop");
    }

    public void stopStreaming() {
        torrentStreamServer.stopStream();
    }

    public void cleanHandler(){
        torrentStreamServer.stopStream();
//        instance=null;
        setStreamingUrl(null);
        setError(null);
        setState(null);
        setProgressStatus(null);
        setTorrentFile(null);
        isServiceActive=false;
        Log.d(TAG,"clearAll");
    }



    public void setTorrentFile(Torrent torrentFile) {
        this.torrentFile.setValue(torrentFile);
    }


    public void setProgressStatus(StreamStatus progressStatus) {
        this.progressStatus.setValue(progressStatus);
    }


    public void setStreamingUrl(String streamingUrl) {
        this.streamingUrl.setValue(streamingUrl);
    }


    public void setState(String state) {
        this.state.setValue(state);
    }


    public void setError(String error) {
        this.error.setValue(error);
    }
}
