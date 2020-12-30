package com.victoriya.tortube.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;

import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.victoriya.tortube.R;
import com.victoriya.tortube.model.Files;
import com.victoriya.tortube.torrentstreamserver.TorrentServerListener;
import com.victoriya.tortube.torrentstreamserver.TorrentStreamNotInitializedException;
import com.victoriya.tortube.torrentstreamserver.TorrentStreamServer;
import com.victoriya.tortube.ui.main.CheckingDialogFragment;
import com.victoriya.tortube.ui.main.MainActivity;
import com.victoriya.tortube.viewmodel.UpdateShareViewModel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.victoriya.tortube.MyApplication.CHANNEL_ID;
import static com.victoriya.tortube.ui.main.MainActivity.MAGNET_LINK;

public class StreamingService extends Service{

    private static final String TAG=StreamingService.class.getSimpleName();
    public static final String STOP_SERVICE="stop_service";

    private ServiceHandler serviceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHandler=ServiceHandler.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()==STOP_SERVICE){
            serviceHandler.cleanHandler();
            stopSelf();
        }
        else {
            foregroundService();
            serviceHandler.onHandleIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void foregroundService() {
        Intent serviceStopperIntent = new Intent(this, StreamingService.class);
        serviceStopperIntent.setAction(STOP_SERVICE);
        PendingIntent serviceStopperPendingIntent=
                PendingIntent.getService(this, 0, serviceStopperIntent, 0);
        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Start Server")
                        .setContentText("Fetching Data...")
                        .setSmallIcon(R.drawable.ic_video_notification)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .addAction(new NotificationCompat.Action(R.drawable.stop_service,"Stop",serviceStopperPendingIntent))
                        .build();
        startForeground(1110,notification);
    }

    @Override
    public void onDestroy() {
        serviceHandler.cleanHandler();
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}