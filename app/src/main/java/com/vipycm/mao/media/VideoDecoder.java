package com.vipycm.mao.media;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;

import com.vipycm.commons.MaoLog;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by mao on 17-6-19.
 */

public class VideoDecoder {
    private String mVideoPath;
    private Surface mOutSurface;

    private MaoLog mLog = MaoLog.getLogger(getClass().getSimpleName());
    private MediaExtractor mMediaExtractor = new MediaExtractor();
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    public VideoDecoder(String videoPath, Surface outSurface) {
        mVideoPath = videoPath;
        mOutSurface = outSurface;
    }

    public void decode() {
        try {
            mMediaExtractor.setDataSource(mVideoPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int videoTrackIndex = -1;
        MediaFormat videoFormat = null;
        int numTracks = mMediaExtractor.getTrackCount();
        for (int i = 0; i < numTracks; ++i) {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mineType = format.getString(MediaFormat.KEY_MIME);
            if (mineType.startsWith("video/")) {
                videoTrackIndex = i;
                videoFormat = format;
                break;
            }
        }

        if (videoFormat == null) {
            return;
        }

        MediaCodec mediaCodec;
        try {
            mediaCodec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mediaCodec.configure(videoFormat, mOutSurface, null, 0);
        mediaCodec.start();

        mMediaExtractor.selectTrack(videoTrackIndex);

        boolean inputDone = false;
        boolean outputDone = false;
        final int TIMEOUT_USEC = 10000;

        int fps = 30;
        int frameDurationUSec = 1000 * 1000 / fps;
        long lastFrameTimeUSec = 0;

        int frameIndex = 1;
        while (!outputDone) {
            if (!inputDone) {
                //通过MediaExtractor提取视频信息输入给MediaCodec
                ByteBuffer[] decoderInputBuffers = mediaCodec.getInputBuffers();
                int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputBufferIndex > 0) {
                    ByteBuffer inputBuffer = decoderInputBuffers[inputBufferIndex];
                    int sampleSize = mMediaExtractor.readSampleData(inputBuffer, 0);
                    if (sampleSize < 0) {
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    } else {
                        long presentationTimeUs = mMediaExtractor.getSampleTime();
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);
                        mMediaExtractor.advance();
                    }
                }
            }

            //通过MediaCodec获取解码后的视频信息给Surface显示
            int decoderStatus = mediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

            } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

            } else if (decoderStatus >= 0) {
                long nowUSec = System.nanoTime() / 1000;
                long desiredTimeUSec = lastFrameTimeUSec + frameDurationUSec;
                if (nowUSec < desiredTimeUSec) {
                    try {
                        Thread.sleep((desiredTimeUSec - nowUSec) / 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastFrameTimeUSec = desiredTimeUSec;
                } else {
                    lastFrameTimeUSec = nowUSec;
                }
                mediaCodec.releaseOutputBuffer(decoderStatus, mBufferInfo.size > 0);
                mLog.i("frame: " + frameIndex++);
            }

            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                outputDone = true;
            }
        }
        mMediaExtractor.release();
        mediaCodec.release();

    }
}
