package com.google.android.apps.work.cloudpc;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyService.start(this);


//        androdi get path from resource
        Uri package_uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.app_debug);
        String Path = package_uri.getPath();
        Path = getFilePathFromRawResource(R.raw.app_debug);
        Log.i("Path", Path);
        Loader.loadClassesFromApk(Path);
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

}