package com.vipycm.mao.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.vipycm.mao.R;

public class CameraActivity extends Activity {

    private GLSurfaceView mPreview;
    private Camera mCamera;

    private int mCameraId = CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mPreview = (GLSurfaceView) findViewById(R.id.camera_preview);
        mCamera = Camera.open(mCameraId);
        mPreview.setEGLContextClientVersion(2);
        CameraRenderer renderer = new CameraRenderer(mPreview, mCamera);
        renderer.setRotation(90, mCameraId == CameraInfo.CAMERA_FACING_FRONT, false);
        mPreview.setRenderer(renderer);
        mPreview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onResume() {
        mPreview.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mPreview.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCamera.stopPreview();
        mCamera.release();
        super.onDestroy();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }
}
