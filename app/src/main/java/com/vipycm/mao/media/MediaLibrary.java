package com.vipycm.mao.media;

/**
 * Created by mao on 17-8-15.
 */

public class MediaLibrary {
    private static boolean mIsInitialized = false;

    public synchronized static void init() {
        if (mIsInitialized) {
            return;
        }
        System.loadLibrary("mao-media");
        nativeInit();
        mIsInitialized = true;
    }

    private static native void nativeInit();
}
