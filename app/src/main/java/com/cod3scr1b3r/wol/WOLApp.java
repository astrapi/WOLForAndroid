package com.cod3scr1b3r.wol;

import android.app.Application;

/**
 * Created by Eyal on 04-12-14.
 */
public class WOLApp extends Application {

    private AppDataStore mDataStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataStore = new AppDataStore(this);
    }

    public AppDataStore getDataStrore(){
        return mDataStore;
    }
}
