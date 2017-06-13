package com.vipycm.mao.cameranew;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.camera.OpenGlUtils;
import com.vipycm.mao.camera.TextureRotationUtil;
import com.vipycm.mao.cameranew.filter.CameraFilter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mao on 17-6-13.
 */

public class CameraView extends GLSurfaceView implements Renderer, OnFrameAvailableListener {

    MaoLog mLog = MaoLog.getLogger(getClass().getSimpleName());
    private Camera mCamera;
    private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

    private FloatBuffer mCubeBuffer;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mMVPBuffer;

    private int mTextureId = 0;
    private boolean mNeedUpdateTexImage = false;
    private SurfaceTexture mSurfaceTexture;
    private CameraFilter mCameraFilter = new CameraFilter();

    private final Queue<Runnable> mRunOnDraw = new LinkedList<>();

    private static final float[] CUBE = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeBuffer.put(CUBE);

        float[] textureCords = TextureRotationUtil.getRotation(90, mCameraId == CameraInfo.CAMERA_FACING_FRONT, false);
        mTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.put(textureCords);

        mMVPBuffer = ByteBuffer.allocateDirect(OpenGlUtils.IDENTITY_MATRIX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMVPBuffer.put(OpenGlUtils.IDENTITY_MATRIX);
    }

    @Override
    public void onResume() {
        mLog.i("onResume");
        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open(mCameraId);
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    try {
                        Camera.Parameters param = mCamera.getParameters();
                        param.setPreviewSize(1280, 720);//TODO
                        if (param.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            param.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }
                        mCamera.setParameters(param);
                        int[] textures = new int[1];
                        GLES20.glGenTextures(1, textures, 0);
                        mTextureId = textures[0];
                        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
                        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                        mSurfaceTexture = new SurfaceTexture(mTextureId);
                        mCamera.setPreviewTexture(mSurfaceTexture);
                        mSurfaceTexture.setOnFrameAvailableListener(CameraView.this);
                        mCamera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        mLog.i("onPause");
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mLog.i("onSurfaceCreated");
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mCameraFilter.loadProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mLog.i("onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mLog.i("onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        synchronized (this) {
            if (mNeedUpdateTexImage && mSurfaceTexture != null) {
                mSurfaceTexture.updateTexImage();
            }
            mNeedUpdateTexImage = false;
        }
        runAll(mRunOnDraw);
        if (mTextureId != 0) {
            mCameraFilter.onDraw(mTextureId, mCubeBuffer, mTextureBuffer);
        }
    }

    @Override
    public synchronized void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mLog.i("onFrameAvailable");
        mNeedUpdateTexImage = true;
        requestRender();
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    private void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }
}
