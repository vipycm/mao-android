package com.vipycm.commons;

import android.util.Log;

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
}
