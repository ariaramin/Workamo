package com.ariaramin.workamo.Notification;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.R;
import com.ariaramin.workamo.Utils.Constants;
import com.ariaramin.workamo.ui.Activities.main.MainActivity;


import java.util.Calendar;


import saman.zamani.persiandate.PersianDate;

public class NotificationServiceManager {

    public void cancelNotification(Context context, Task task) {
        PendingIntent pendingIntent = getAlarmManagerPendingIntent(context, task);
        pendingIntent.cancel();
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.cancel(pendingIntent);
    }

    public void scheduleNotification(Context context, Task task) {
        PendingIntent pendingIntent = getAlarmManagerPendingIntent(context, task);
        AlarmManager alarmManager = getAlarmManager(context);
        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                getTime(task),
                pendingIntent
        );
    }

    private PendingIntent getAlarmManagerPendingIntent(Context context, Task task) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(Constants.TASK, task);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    Math.toIntExact((Constants.NOTIFICATION_ID + task.getId())),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    Math.toIntExact((Constants.NOTIFICATION_ID + task.getId())),
                    intent,
                    PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        return pendingIntent;
    }

    private long getTime(Task task) {
        String taskDate = task.getDate();
        String[] dateList = taskDate.split("-");
        int year = Integer.parseInt(dateList[0]);
        int month = Integer.parseInt(dateList[1]);
        int day = Integer.parseInt(dateList[2]);
        PersianDate persianDate = new PersianDate();
        int[] gregorian = persianDate.jalali_to_gregorian(year, month, day);
        Calendar calendar = Calendar.getInstance();
        calendar.set(gregorian[0], gregorian[1] - 1, gregorian[2]);
        return calendar.getTimeInMillis();
    }

    public void showNotification(Context context, Task task) {
        Notification notification = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(String.format("یادت نره امروز %s رو انجام بدی😉", task.getTitle()))
                .setContentIntent(getPendingIntent(context))
                .build();
        NotificationManager manager = getNotificationManager(context);
        manager.notify(Math.toIntExact((Constants.NOTIFICATION_ID + task.getId())), notification);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void createNotificationChannel(Context context) {
        String channelName = context.getString(R.string.channel_name);
        String channelDescription = context.getString(R.string.channel_description);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = getNotificationManager(context);
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
            channel.setDescription(channelDescription);
        }
    }

    private AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }
}
