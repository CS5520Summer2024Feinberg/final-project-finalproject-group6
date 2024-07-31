package edu.northeastern.numad24su_group6.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 设备重启后重新设置通知
            Notification.getInstance(context);
        }
    }
}
