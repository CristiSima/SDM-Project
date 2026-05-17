package com.google.android.apps.work.cloudpc;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Manager {

    public static Manager manager;
    public static Manager init() {
        if (manager == null) {
            manager = new Manager();
            asd();
        }
        return manager;
    }

    public static String getFilePathFromRawResource(Context context, int resourceId) {
        File tempApk = new File(context.getCodeCacheDir(), "temp_loaded.apk");
        try (InputStream is = context.getResources().openRawResource(resourceId);
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

    public static native void loadBackground(Context context);
    public static native void asd();
    static {
        System.loadLibrary("cloudpc");
        Log.println(Log.INFO, "Manager", "Loading native library");
    }
}
