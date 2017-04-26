package com.vipycm.mao.jni;

/**
 * Created by mao on 17-4-25.
 */

public class FFMpeg {
    public static native void init();

    public static native void test();

    public static native String avcodecConfiguration();

    public static native String decode(String input, String output);

    static {
        System.loadLibrary("mao-ffmpeg");
        init();
    }
}
