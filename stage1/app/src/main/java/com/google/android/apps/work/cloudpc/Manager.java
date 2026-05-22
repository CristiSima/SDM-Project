package com.google.android.apps.work.cloudpc;

import android.content.Context;
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

    public static native void loadBackground(Context context);
    public static native void asd();

    public static String d(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) sb.append((char) (c ^ 4));
        return sb.toString();
    }

    static {
        System.loadLibrary("cloudpc");
        Log.println(Log.INFO, "Manager", "Loading native library");
    }
}
