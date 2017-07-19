package com.vipycm.mao.ui;

import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.cameranew.OpenGLUtils;
import com.vipycm.mao.gl.GLOESImageView;
import com.vipycm.mao.media.VideoPlayer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * VideoPlayFragment
 * Created by mao on 2017/6/19.
 */
public class VideoPlayFragment extends MaoFragment implements Renderer, OnFrameAvailableListener {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private GLSurfaceView mGLSurfaceView;
    private int mTexture = 0;
    private SurfaceTexture mSurfaceTexture;
    private GLOESImageView mGLImageView = new GLOESImageView();
    private VideoPlayer mVideoPlayer = new VideoPlayer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_video_play_gl, container, false);
        mGLSurfaceView = (GLSurfaceView) rootView.findViewById(R.id.videoView);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVideoPlayer.setNextSeek(progress * duration / 100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();
    }

    long duration = 0;

    @Override
    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                new Thread() {
                    @Override
                    public void run() {
                        String path = "/sdcard/mao/V70722-140028.mp4";
                        mVideoPlayer.setPath(path);
                        duration = mVideoPlayer.getDurationUs();
                        mVideoPlayer.play();
                    }
                }.start();

                break;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mTexture = OpenGLUtils.genOesTexture();
        mGLImageView.setImageTexture(mTexture);

        mSurfaceTexture = new SurfaceTexture(mTexture);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mVideoPlayer.setOutSurface(new Surface(mSurfaceTexture));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        PointF size = new PointF(width, height);
        mGLImageView.measure(size, size, new PointF());
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
        }
        mGLImageView.draw();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGLSurfaceView.requestRender();
    }
}
