package com.vipycm.mao.cameragl;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.vipycm.mao.camera.TextureRotationUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by yangchangmao on 17-4-20.
 */

public class CameraRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    public static final int NO_IMAGE = -1;

    private int mGLTextureId = NO_IMAGE;
    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mGLTextureBuffer;

    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;
    CameraFilter mFilter;

    private boolean mUpdateST = false;

    private GLSurfaceView mView;

    static final float CUBE[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f,
    };

    public CameraRenderer(GLSurfaceView view, Camera camera) {
        mCamera = camera;
        mView = view;

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);
        float[] textureCords = TextureRotationUtil.getRotation(90, true, false);
        mGLTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(textureCords).position(0);

        mFilter = new CameraFilter();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mUpdateST = true;
        mView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initTexture();
        mFilter.init();
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
    }

    private void initTexture() {
        int[] hTex = new int[1];
        GLES20.glGenTextures(1, hTex, 0);
        mGLTextureId = hTex[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mGLTextureId);
        mSurfaceTexture = new SurfaceTexture(mGLTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            Camera.Parameters param = mCamera.getParameters();
            param.setPreviewSize(1920, 1080);//TODO
            if (param.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                param.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(param);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (mUpdateST) {
                mSurfaceTexture.updateTexImage();
                mUpdateST = false;
            }
        }
        mFilter.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer);
    }

    public void setRotation(final int rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] textureCords = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);
        mGLTextureBuffer = ByteBuffer.allocateDirect(textureCords.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(textureCords).position(0);
    }
}
