package com.vipycm.mao.ui;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;

import java.util.Arrays;

/**
 * CameraFragment
 * Created by mao on 2017/4/7.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class CameraFragment extends MaoFragment implements SurfaceTextureListener, OnCheckedChangeListener {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private static final String LENS_FACING_FRONT = String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
    private static final String LENS_FACING_BACK = String.valueOf(CameraCharacteristics.LENS_FACING_BACK);

    TextView txt_content;
    TextureView ttv_camera;
    ToggleButton tbtn_camera;

    CameraManager mCameraManager;
    HandlerThread mHandlerThread;
    Handler mHandler;
    CaptureRequest.Builder mPreviewBuilder;

    String mCurLens = LENS_FACING_BACK;
    CameraDevice mCurCameraDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        txt_content.setText(this.getClass().getSimpleName());

        mCameraManager = (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);

        mHandlerThread = new HandlerThread("CameraFragment_HandlerThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        ttv_camera = (TextureView) rootView.findViewById(R.id.ttv_camera);
        ttv_camera.setSurfaceTextureListener(this);

        tbtn_camera = (ToggleButton) rootView.findViewById(R.id.tbtn_camera);
        tbtn_camera.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        closeCurCamera();
        super.onDestroyView();

    }

    @Override
    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                closeCurCamera();
                break;
        }
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(mCurLens);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void openCamera(String cameraId) {
        try {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            if (ActivityCompat.checkSelfPermission(getContext(), permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCameraManager.openCamera(cameraId, mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCurCamera() {
        if (mCurCameraDevice != null) {
            mCurCameraDevice.close();
            mCurCameraDevice = null;
        }
    }

    private void startPreview(CameraDevice camera) {
        SurfaceTexture texture = ttv_camera.getSurfaceTexture();
        texture.setDefaultBufferSize(ttv_camera.getWidth(), ttv_camera.getHeight());
        Surface surface = new Surface(texture);
        try {
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);
        try {
            camera.createCaptureSession(Arrays.asList(surface), mSessionStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCurCameraDevice = camera;
            startPreview(camera);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
        }

        @Override
        public void onError(CameraDevice camera, int error) {
        }
    };

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            try {
                //session.capture(mPreviewBuilder.build(), mSessionCaptureCallback, mHandler);
                session.setRepeatingRequest(mPreviewBuilder.build(), mSessionCaptureCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
        }
    };

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String newLens = isChecked ? LENS_FACING_FRONT : LENS_FACING_BACK;
        if (newLens.equals(mCurLens)) {
            return;
        }
        closeCurCamera();
        mCurLens = newLens;
        openCamera(mCurLens);
    }
}
