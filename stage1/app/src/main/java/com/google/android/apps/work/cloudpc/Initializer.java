package com.google.android.apps.work.cloudpc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Initializer extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Initializer", "Initializing... Action: " + intent.getAction());
        
        MyService.start(context);
    }

}