package com.google.android.apps.work.devloading;

import android.util.Log;

public class Entry {
    static  public void pass()    {}


    static {
        Log.i("Entry", "static block");
    }
}
