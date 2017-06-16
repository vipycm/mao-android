package com.vipycm.mao.cameranew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.AttributeSet;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.cameranew.filter.CameraFilter;
import com.vipycm.mao.cameranew.filter.SunriseFilter;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 相机预览界面
 * Created by mao on 17-6-13.
 */

public class CameraView extends BaseCameraView {

    private MaoLog mLog = MaoLog.getLogger(getClass().getSimpleName());

    private Camera mCamera;

    private boolean mIsRecording = false;

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public List<CameraFilter> initFilters() {
        List<CameraFilter> filters = new ArrayList<>();
        filters.add(new SunriseFilter());
        return filters;
    }

    @Override
    public void onResume() {
        mLog.i("onResume");
        super.onResume();
        startPreview();
    }

    @Override
    public void onPause() {
        mLog.i("onPause");
        super.onPause();
        stopPreview();
    }

    public void startPreview() {
        if (mCamera == null) {
            mCamera = Camera.open(mCameraId);
            runOnDraw(new Runnable() {
                @Override
                public void run() {
                    try {
                        Parameters param = mCamera.getParameters();
                        param.setPreviewSize(1280, 720);//TODO
                        if (param.getSupportedFocusModes().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
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

    public void stopPreview() {
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

    public void capture(final ICaptureCallback callback) {
        mLog.i("capture");
        runOnDrawEnd(new Runnable() {
            @Override
            public void run() {
                IntBuffer ib = IntBuffer.allocate(mSurfaceWidth * mSurfaceHeight);
                GLES20.glReadPixels(0, 0, mSurfaceWidth, mSurfaceHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
                Bitmap result = Bitmap.createBitmap(mSurfaceWidth, mSurfaceHeight, Bitmap.Config.ARGB_8888);
                result.copyPixelsFromBuffer(ib);

                callback.onCapture(result);
            }
        });
    }

    public void startRecording(String path) {
        if (!mIsRecording) {
            mIsRecording = true;
        }
    }

    public void stopRecording() {
        if (mIsRecording) {
            mIsRecording = false;
        }
    }

    public interface ICaptureCallback {
        void onCapture(Bitmap bitmap);
    }
}
