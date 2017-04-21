package com.vipycm.mao.ui;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.cameragl.CameraRenderer;

/**
 * 使用GLSurfaceView预览相机
 * Created by mao on 2017/4/21.
 */
public class CameraGLFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private GLSurfaceView mPreview;
    private Camera mCamera;

    private int mCameraId = CameraInfo.CAMERA_FACING_BACK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_cameragl, container, false);

        mPreview = (GLSurfaceView) rootView.findViewById(R.id.camera_preview);
        mCamera = Camera.open(mCameraId);
        mPreview.setEGLContextClientVersion(2);
        CameraRenderer renderer = new CameraRenderer(mPreview, mCamera);
        renderer.setRotation(90, mCameraId == CameraInfo.CAMERA_FACING_FRONT, false);
        mPreview.setRenderer(renderer);
        mPreview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreview.onResume();
    }

    @Override
    public void onPause() {
        mPreview.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        mCamera.stopPreview();
        mCamera.release();
        super.onDestroyView();

    }
}
