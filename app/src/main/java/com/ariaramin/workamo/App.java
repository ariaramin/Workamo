package com.ariaramin.workamo;

import android.app.Application;

import com.ariaramin.workamo.Notification.NotificationServiceManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationServiceManager notificationManager = new NotificationServiceManager();
        notificationManager.createNotificationChannel(this);
    }
}
