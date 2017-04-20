package com.vipycm.mao.camera;

/**
 * Created by yangchangmao on 17-4-20.
 */

public class YuvDecoder {
    static {
        System.loadLibrary("yuv-decoder");
    }

    public static native void YUVtoRBGA(byte[] yuv, int width, int height, int[] out);

    public static native void YUVtoARBG(byte[] yuv, int width, int height, int[] out);
}
