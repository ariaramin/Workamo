package com.ariaramin.workamo.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.Utils.Constants;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Task task = extras.getParcelable(Constants.TASK);
        NotificationServiceManager notificationManager = new NotificationServiceManager();
        if (task != null) {
            notificationManager.showNotification(context, task);
        }
    }
}
