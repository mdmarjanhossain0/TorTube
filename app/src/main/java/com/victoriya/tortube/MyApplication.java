package com.victoriya.tortube;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.victoriya.tortube.service.StreamingHandler;

public class MyApplication extends Application{


    public static final String CHANNEL_ID="foreground_notification";

    @Override
    public void onCreate() {
        super.onCreate();

        serviceHandlerInit(this);
        createNotificationChannel();
    }

    private void serviceHandlerInit(MyApplication application) {
        StreamingHandler.getInstance(application);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
