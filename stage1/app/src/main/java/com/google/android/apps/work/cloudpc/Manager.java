package com.google.android.apps.work.cloudpc;

import android.util.Log;

public class Manager {

    public static Manager manager;
    public static Manager init() {
        if (manager == null) {
            manager = new Manager();

            asd();
        }
        return manager;
    }

    public static native void asd();
    static {
        System.loadLibrary("cloudpc");
        Log.println(Log.INFO, "Manager", "Loading native library");
    }
}
