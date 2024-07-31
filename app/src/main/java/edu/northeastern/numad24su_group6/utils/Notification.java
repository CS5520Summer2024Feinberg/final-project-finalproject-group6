package edu.northeastern.numad24su_group6.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import edu.northeastern.numad24su_group6.MainActivity;
import edu.northeastern.numad24su_group6.R;

public class Notification {

    private static Notification instance;
    private Timer timer;
    private Context context;

    private Notification(Context context) {
        this.context = context;
        timer = new Timer();
        scheduleNotification(7, 0, "It's breakfast time!");
        scheduleNotification(12, 0, "It's lunch time!");
        scheduleNotification(18, 0, "It's dinner time!");
    }

    public static synchronized Notification getInstance(Context context) {
        if (instance == null) {
            instance = new Notification(context);
        }
        return instance;
    }

    private void scheduleNotification(int hour, int minute, String message) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (delay < 0) {
            delay += 24 * 60 * 60 * 1000;  // Add a day if the time has already passed
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showNotification(message);
            }
        }, delay, 24 * 60 * 60 * 1000);  // Repeat every 24 hours
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "meal_notification_channel";
        NotificationChannel channel = new NotificationChannel(channelId, "Meal Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("Meal Time Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
