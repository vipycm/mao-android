package com.vipycm.mao.cameranew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.vipycm.commons.FileUtils;
import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.camera.CaptureButton;
import com.vipycm.mao.cameranew.CameraView.ICaptureCallback;

/**
 * Created by mao on 17-6-13.
 */

public class CameraActivity extends Activity {

    private MaoLog mLog = MaoLog.getLogger(getClass().getSimpleName());
    private CameraView mCameraView;

    private final String mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mao/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_new);
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        CaptureButton captureButton = (CaptureButton) findViewById(R.id.capture_button);
        captureButton.setCaptureButtonListener(mCaptureButtonListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }

    CaptureButton.ICaptureButtonListener mCaptureButtonListener = new CaptureButton.ICaptureButtonListener() {

        @Override
        public void onClick() {
            mLog.i("capture start");
            mCameraView.capture(new ICaptureCallback() {
                @Override
                public void onCapture(Bitmap bitmap) {
                    String path = mSavePath + System.currentTimeMillis() + ".jpg";
                    FileUtils.saveImage(path, bitmap);
                    mLog.i("onCapture:" + path);
                }
            });
        }

        @Override
        public void onLongPress() {
            String path = mSavePath + System.currentTimeMillis() + ".mp4";
            mCameraView.startRecording(path);
        }

        @Override
        public void onLongPressEnd() {
            mCameraView.stopRecording();
        }
    };
}
