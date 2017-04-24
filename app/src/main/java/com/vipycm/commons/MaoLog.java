package com.vipycm.commons;

import android.content.Context;

import com.vipycm.commons.xlog.Log;
import com.vipycm.commons.xlog.Xlog;
import com.vipycm.mao.BuildConfig;
import com.vipycm.mao.MaoApp;

import java.io.File;

/**
 * 日志工具
 * Created by mao on 16-12-29.
 */
public class MaoLog {
    private final String mTag;

    private MaoLog(String tag) {
        mTag = tag;
    }

    public static MaoLog getLogger(String tag) {
        return new MaoLog(tag);
    }

    public void d(String msg) {
        Log.d(mTag, msg);
    }

    public void i(String msg) {
        Log.i(mTag, msg);
    }

    public void w(String msg) {
        Log.w(mTag, msg);
    }

    public void e(String msg) {
        Log.e(mTag, msg);
    }

    public void d(int msg) {
        Log.d(mTag, String.valueOf(msg));
    }

    public void i(int msg) {
        Log.i(mTag, String.valueOf(msg));
    }

    public void w(int msg) {
        Log.w(mTag, String.valueOf(msg));
    }

    public void e(int msg) {
        Log.e(mTag, String.valueOf(msg));
    }

    public static synchronized void initLog() {
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");

        int logLevel = BuildConfig.DEBUG ? Xlog.LEVEL_DEBUG : Xlog.LEVEL_INFO;
        Context context = MaoApp.getContext();
        File logDir = context.getExternalFilesDir("log");
        if (logDir == null) {
            logDir = new File(context.getFilesDir(), "log");
        }
        String logPath = logDir.getAbsolutePath();
        String logName = "log";
        Xlog.appenderOpen(logLevel, Xlog.AppednerModeAsync, "", logPath, logName);
        Xlog.setConsoleLogOpen(BuildConfig.DEBUG);
        Log.setLogImp(new Xlog());
    }

    public static synchronized void unInitLog() {
        Log.appenderClose();
    }

    public static void flushLog(boolean isSync) {
        Log.appenderFlush(isSync);
    }
}
