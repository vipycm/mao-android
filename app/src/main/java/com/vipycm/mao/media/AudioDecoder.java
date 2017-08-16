package com.vipycm.mao.media;

import java.nio.ByteBuffer;

/**
 * Created by mao on 17-8-15.
 */

public class AudioDecoder {
    private long mAudioDecoder;

    public AudioDecoder() {
        mAudioDecoder = nativeCreateAudioDecoder();
    }

    public void addBuffer(ByteBuffer buffer) {
        nativeAddBuffer(mAudioDecoder, buffer);
    }

    public int prepare(String path) {
        return nativePrepare(mAudioDecoder, path);
    }

    public int decode() {
        return nativeDecode(mAudioDecoder);
    }

    public void release() {
        nativeRelease(mAudioDecoder);
    }

    private native long nativeCreateAudioDecoder();

    public native void nativeAddBuffer(long decoder, ByteBuffer buffer);

    public native int nativePrepare(long decoder, String path);

    public native int nativeDecode(long decoder);

    public native void nativeRelease(long decoder);
}
