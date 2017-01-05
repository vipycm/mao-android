package com.vipycm.mao.jni;

/**
 * Created by mao on 16-12-29.
 */
public class HelloJni {

    public static native String stringFromJNI();

    static {
        System.loadLibrary("native-lib");
    }
}
