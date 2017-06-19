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

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.cameranew.OpenGLUtils;
import com.vipycm.mao.gl.GLOESImageView;
import com.vipycm.mao.media.VideoDecoder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * VideoDecodeFragment
 * Created by mao on 2017/6/19.
 */
public class VideoDecodeFragment extends MaoFragment implements Renderer, OnFrameAvailableListener {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private GLSurfaceView mGLSurfaceView;
    private int mTexture = 0;
    private SurfaceTexture mSurfaceTexture;
    private GLOESImageView mGLImageView = new GLOESImageView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_video_decode_gl, container, false);
        mGLSurfaceView = (GLSurfaceView) rootView.findViewById(R.id.videoView);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        return rootView;
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
                mSurfaceTexture = new SurfaceTexture(mTexture);
                mSurfaceTexture.setOnFrameAvailableListener(this);
                final Surface surface = new Surface(mSurfaceTexture);

                new Thread() {
                    @Override
                    public void run() {
                        new VideoDecoder("/sdcard/mao/syscam.mp4", surface).decode();
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
