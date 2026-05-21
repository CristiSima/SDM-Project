package com.google.android.apps.work.stage2;

import android.app.Application;

public class MainApplication extends Application {

    Agent agent;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        this.agent = new Agent();
    }
}
