package com.victoriya.tortube.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.victoriya.tortube.R;

import static com.victoriya.tortube.MyApplication.CHANNEL_ID;

public class StreamingService extends Service{

    private static final String TAG=StreamingService.class.getSimpleName();
    public static final String STOP_SERVICE="stop_service";

    private StreamingHandler streamingHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        streamingHandler = StreamingHandler.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()==STOP_SERVICE){
            streamingHandler.cleanHandler();
            stopSelf();
        }
        else {
            foregroundService();
            streamingHandler.onHandleIntent(intent);
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
        streamingHandler.cleanHandler();
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}