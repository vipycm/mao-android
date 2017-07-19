package com.vipycm.mao.media;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.view.Surface;

import com.vipycm.commons.MaoLog;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by mao on 17-7-19.
 */

public class VideoPlayer {
    private MaoLog mLog = MaoLog.getLogger(getClass().getSimpleName());

    public static final int BUFFER_TIMEOUT_US = 10000;

    private String mPath;
    private Surface mOutSurface;
    private long mDurationUs;
    private long mNextSeek;

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;

        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        String durationStr = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        mDurationUs = Long.parseLong(durationStr) * 1000;
    }

    public long getDurationUs() {
        return mDurationUs;
    }

    public void setOutSurface(Surface outSurface) {
        mOutSurface = outSurface;
    }

    public void setNextSeek(long nextSeek) {
        mNextSeek = nextSeek;
    }

    public void play() {
        if (mPath == null) {
            mLog.e("video path is null");
            return;
        }
        if (mOutSurface == null) {
            mLog.e("out surface is null");
            return;
        }

        mNextSeek = -1;
        MediaExtractor mediaExtractor;
        MediaCodec mediaCodec;
        MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

        try {
            mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
            mLog.e("set data source error");
            return;
        }

        int videoTrackIndex = -1;
        MediaFormat videoFormat = null;
        int trackCount = mediaExtractor.getTrackCount();
        for (int i = 0; i < trackCount; ++i) {
            MediaFormat format = mediaExtractor.getTrackFormat(i);
            String mineType = format.getString(MediaFormat.KEY_MIME);
            if (mineType.startsWith("video/")) {
                videoTrackIndex = i;
                videoFormat = format;
                break;
            }
        }

        if (videoTrackIndex == -1) {
            mLog.e("no video track");
            return;
        }

        try {
            mediaCodec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME));
        } catch (IOException e) {
            e.printStackTrace();
            mLog.e("create decoder error");
            return;
        }
        mediaCodec.configure(videoFormat, mOutSurface, null, 0);
        mediaCodec.configure(videoFormat, null, null, 0);
        mediaCodec.start();

        mediaExtractor.selectTrack(videoTrackIndex);

        boolean inputDone = false;
        boolean outputDone = false;

        int frameIndex = 1;
        ByteBuffer[] decoderInputBuffers = mediaCodec.getInputBuffers();

        long sampleTime = -1;
        long presentationTimeUs = -1;
        while (!outputDone) {
            if (!inputDone) {
                //通过MediaExtractor提取视频信息输入给MediaCodec
                int bufferIndex = mediaCodec.dequeueInputBuffer(BUFFER_TIMEOUT_US);
                if (bufferIndex > 0) {
                    ByteBuffer inputBuffer = decoderInputBuffers[bufferIndex];
                    int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                    if (sampleSize < 0) {
                        mLog.i("queue end of stream");
                        mediaCodec.queueInputBuffer(bufferIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    } else {
                        long newSampleTime = mediaExtractor.getSampleTime();
                        if (mNextSeek >= 0) {
                            mediaExtractor.seekTo(mNextSeek, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                            mNextSeek = -1;
                            presentationTimeUs = System.nanoTime() / 1000;
                        } else {
                            mediaExtractor.advance();
                            if (sampleTime == -1) {
                                presentationTimeUs = System.nanoTime() / 1000;
                            } else {
                                presentationTimeUs = presentationTimeUs + newSampleTime - sampleTime;
                            }
                        }
                        sampleTime = newSampleTime;
                        mLog.i("presentationTimeUs " + presentationTimeUs + " put");
                        mediaCodec.queueInputBuffer(bufferIndex, 0, sampleSize, presentationTimeUs, 0);
                    }
                } else if (bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    mLog.i("queue try again later");
                }
            }

            //通过MediaCodec获取解码后的视频信息给Surface显示
            int bufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, BUFFER_TIMEOUT_US);
            if (bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                mLog.i("dequeue try again later");
            } else if (bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

            } else if (bufferIndex >= 0) {

                long nowUSec = System.nanoTime() / 1000;
                long desiredTimeUSec = mBufferInfo.presentationTimeUs;
                mLog.i("presentationTimeUs " + desiredTimeUSec);
                if (nowUSec < desiredTimeUSec) {
                    try {
                        Thread.sleep((desiredTimeUSec - nowUSec) / 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mediaCodec.releaseOutputBuffer(bufferIndex, mBufferInfo.size > 0);
                mLog.i("frame: " + frameIndex++);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    mLog.i("dequeue end of stream");
                    outputDone = true;
                }
            }
        }
        mediaExtractor.release();
        mediaCodec.release();
    }
}
