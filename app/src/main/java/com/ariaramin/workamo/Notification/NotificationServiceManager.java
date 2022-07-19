package com.ariaramin.workamo.Notification;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.ariaramin.workamo.Database.Task;
import com.ariaramin.workamo.R;
import com.ariaramin.workamo.Utils.Constants;
import com.ariaramin.workamo.ui.Activities.main.MainActivity;


import java.util.Calendar;


import saman.zamani.persiandate.PersianDate;

public class NotificationServiceManager {

    public void scheduleNotification(Context context, Task task) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(Constants.TASK, task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                Math.toIntExact((Constants.NOTIFICATION_ID + task.getId())),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                getTime(task),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
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
        calendar.set(gregorian[0], gregorian[1] - 1, gregorian[2], 0, 0);
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

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }
}
