package com.victoriya.tortube.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.victoriya.tortube.R;
import com.victoriya.tortube.torrentstreamserver.TorrentServerListener;
import com.victoriya.tortube.torrentstreamserver.TorrentStreamServer;
import com.victoriya.tortube.ui.main.MainActivity;
import com.victoriya.tortube.viewmodel.UpdateShareViewModel;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.victoriya.tortube.ui.main.MainActivity.MAGNET_LINK;

public class ServiceHandler implements TorrentServerListener {
    private static final String TAG=StreamingService.class.getSimpleName();

    private TorrentStreamServer torrentStreamServer;
    private SharedPreferences pref;
    private Activity activity;
    private static ServiceHandler instance;
    private UpdateShareViewModel shareViewModel;


    public MutableLiveData<Torrent> torrentFile =new MutableLiveData<>();
    public MutableLiveData<StreamStatus> progressStatus =new MutableLiveData<>();
    public MutableLiveData<String> streamingUrl=new MutableLiveData<>();
    public MutableLiveData<String> state =new MutableLiveData<>();
    public MutableLiveData<String> error=new MutableLiveData<>();



    private ServiceHandler(Activity activity){
        this.activity=activity;
        pref=PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        if(torrentStreamServer==null){
            initServer();
        }


    torrentFile =new MutableLiveData<>();
    progressStatus =new MutableLiveData<>();
    streamingUrl=new MutableLiveData<>();
    state =new MutableLiveData<>();
    error=new MutableLiveData<>();
    }


    public static ServiceHandler getInstance(Activity activity){
        if(instance==null){
            instance=new ServiceHandler(activity);
        }
        return instance;
    }

    public static ServiceHandler getInstance(){
        return instance;
    }


    public void onHandleIntent(Intent intent) {
        Log.d(TAG,Thread.currentThread().getName().toString());
        if(intent!=null){
            shareViewModel=new ViewModelProvider((MainActivity)activity).get(UpdateShareViewModel.class);
            Log.d(TAG,"check"+shareViewModel.hashCode());
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        String magnetLink=intent.getStringExtra(MAGNET_LINK);
        startStreaming(magnetLink);
    }

    private void startStreaming(String magnetLink) {

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

        long bufferSize=pref.getLong(activity.getString(R.string.buffer_size),1L);
        int streamingSpeed=pref.getInt(activity.getString(R.string.streaming_speed),0);
        int seedingSpeed=pref.getInt(activity.getString(R.string.seeding_speed),0);
        boolean saveStatus=pref.getBoolean(activity.getString(R.string.save_status),true);


        TorrentOptions torrentOptions = new TorrentOptions.Builder()
                .saveLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .removeFilesAfterStop(false)
                .prepareSize(1L*1024L*1024L)
                .maxDownloadSpeed(streamingSpeed)
                .maxUploadSpeed(seedingSpeed)
                .build();

        String ipAddress = "127.0.0.1";
        try {
            InetAddress inetAddress = getIpAddress(activity.getApplicationContext());
            if (inetAddress != null) {
                ipAddress = inetAddress.getHostAddress();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        int portNumber=pref.getInt(activity.getString(R.string.port_number),2000);


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
        shareViewModel.setStreamingUrl(url);
        Log.d(TAG, "onServerReady: " + url);
        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setDataAndType(Uri.parse(url), "video/mp4");
        activity.startActivity(intent);*/
    }

    @Override
    public void onStreamPrepared(Torrent torrent) {
        shareViewModel.setState("onStreamPrepared");
        Log.d(TAG,"onStreamPrepared"+"-"+String.valueOf(torrent.getPiecesToPrepare()));
    }

    @Override
    public void onStreamStarted(Torrent torrent) {
        shareViewModel.setTorrentFile(torrent);
        Log.d(TAG, "isValid"+String.valueOf(torrent.getTorrentHandle().isValid()));
        Log.d(TAG,"onTorrentStarted"+String.valueOf(torrent.getFileNames())+Thread.currentThread().getName());
    }

    @Override
    public void onStreamError(Torrent torrent, Exception e) {
        shareViewModel.setError(e.getLocalizedMessage());
        e.printStackTrace();
    }

    @Override
    public void onStreamReady(Torrent torrent) {
        Log.d(TAG, "onStreamReady"+String.valueOf(torrent.getFileNames()));
    }

    @Override
    public void onStreamProgress(Torrent torrent, StreamStatus status) {
        shareViewModel.setProgressStatus(status);
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
        torrentStreamServer.stopTorrentStream();
        instance=null;
        shareViewModel.setStreamingUrl(null);
        shareViewModel.setError(null);
        shareViewModel.setState(null);
        shareViewModel.setProgressStatus(null);
        shareViewModel.setTorrentFile(null);
        Log.d(TAG,"clearAll");
    }
}
