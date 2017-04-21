package com.vipycm.mao.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * 显示相机预览界面
 * Created by mao on 17-4-21.
 */

public class CameraView extends GLSurfaceView {

    private Camera mCamera;
    private CameraRenderer mRenderer;

    private int mCameraId = CameraInfo.CAMERA_FACING_FRONT;

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
        mRenderer = new CameraRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        setUpCamera();
    }

    @Override
    public synchronized void onPause() {
        releaseCamera();
        super.onPause();
    }

    private void setUpCamera() {
        if (mCamera == null) {
            mCamera = Camera.open(mCameraId);
            mRenderer.setRotation(90, mCameraId == CameraInfo.CAMERA_FACING_FRONT, false);
            mRenderer.setUpSurfaceTexture(mCamera);
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera() {
        releaseCamera();
        mCameraId = (mCameraId + 1) % Camera.getNumberOfCameras();
        setUpCamera();
    }
}
