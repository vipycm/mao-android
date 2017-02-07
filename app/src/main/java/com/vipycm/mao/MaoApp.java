package com.vipycm.mao;

import android.app.Application;
import android.content.Context;

import com.vipycm.commons.MaoLog;

/**
 * MaoApp
 * Created by mao on 16-12-29.
 */
public class MaoApp extends Application {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        MaoLog.initLog();
        log.d("onCreate");
    }

    @Override
    public void onTerminate() {
        log.d("onTerminate");
        MaoLog.unInitLog();
        super.onTerminate();
    }

    public static Context getContext() {
        return mContext;
    }
}
