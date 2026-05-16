package com.google.android.apps.work.cloudpc;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyService.start(this);

        asd();
    }

    public static native void asd();
    static {
       System.loadLibrary("cloudpc");
       Log.println(Log.INFO, "MainActivity", "Loading native library");
    }
}