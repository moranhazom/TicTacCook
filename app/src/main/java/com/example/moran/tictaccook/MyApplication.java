package com.example.moran.tictaccook;

import android.app.Application;
import android.content.Context;

/**
 * Created by moran on 01/08/2017.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

}
