package com.vipycm.mao.ui;

import android.databinding.DataBindingUtil;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentSampleBinding;
import com.vipycm.mao.media.AudioDecoder;

import java.nio.ByteBuffer;

/**
 * AudioDecoderFragment
 * Created by mao on 2017/8/15.
 */
public class AudioDecoderFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentSampleBinding mDataBinding;

    private boolean isPlaying = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sample, container, false);
        mDataBinding.setHandler(this);
        mDataBinding.txtContent.setText(this.getClass().getSimpleName());
        mDataBinding.btnOk.setText("play");
        return mDataBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (isPlaying) {
                    isPlaying = false;
                    mDataBinding.btnOk.setText("play");
                } else {
                    isPlaying = true;
                    mDataBinding.btnOk.setText("stop");
                    play();
                }
                break;
        }
    }

    private void play() {
        new Thread() {
            private long startTimeUs = -1;
            private long decodedSize = 0;

            @Override
            public void run() {
                AudioDecoder audioDecoder = new AudioDecoder();
                String path = "/sdcard/mao/audio/MarryYou.mp3";
                int prepare = audioDecoder.prepare(path);
                log.i("prepare:" + prepare);
                if (prepare < 0) {
                    return;
                }
                byte[] buffer = new byte[8192];
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8192);
                audioDecoder.addBuffer(byteBuffer);
                AudioTrack audioTrack = createAudioTrack(SAMPLE_RATE, channelCount);
                audioTrack.play();
                int size = audioDecoder.decode();
                while (size >= 0) {
                    if (size > 0) {
                        byteBuffer.position(0);
                        byteBuffer.get(buffer, 0, size);
                        long nowTimeUs = System.nanoTime() / 1000L;
                        if (startTimeUs == -1) {
                            startTimeUs = nowTimeUs;
                        }
                        long desiredTimeUs = startTimeUs + (decodedSize * 1000L * 1000L / (SAMPLE_RATE * channelCount * 2));

                        long sleepTimeMs = (desiredTimeUs - nowTimeUs) / 1000;
                        if (sleepTimeMs > 20) {
                            SystemClock.sleep(sleepTimeMs - 20);
                        }
                        audioTrack.write(buffer, 0, size);
                        decodedSize += size;
                    }
                    if (!isPlaying) {
                        break;
                    }
                    size = audioDecoder.decode();
                }
                audioDecoder.release();
            }
        }.start();
    }

    private static final int SAMPLE_RATE = 44100;
    private static final int channelCount = 2;

    private AudioTrack createAudioTrack(int sampleRate, int channelCount) {
        int channelConfig = channelCount == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        int bufferSize = ((sampleRate * 2) * channelCount / 100) * 8;//最多缓冲80毫秒的数据
        return new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
    }
}
