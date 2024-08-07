package edu.northeastern.numad24su_group6.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;


public class NotificationScheduler {

    private Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
        setupDailyNotifications();
    }

    private void setupDailyNotifications() {
        scheduleNotification(7, 0, "It's breakfast time!");
        scheduleNotification(12, 0, "It's lunch time!");
        scheduleNotification(18, 0, "It's dinner time!");
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(int hour, int minute, String message) {
        Log.e("check__", "set success: "+ hour+"|"+minute+"|"+message );
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long triggerAtMillis = calendar.getTimeInMillis();
        if (triggerAtMillis < System.currentTimeMillis()) {
            triggerAtMillis += AlarmManager.INTERVAL_DAY;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                hour * 100 + minute,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
