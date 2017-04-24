package com.vipycm.mao.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.vipycm.commons.FileUtils;
import com.vipycm.commons.MaoLog;
import com.vipycm.mao.MaoApp;
import com.vipycm.mao.R;
import com.vipycm.mao.camera.CameraView.ICaptureCallback;
import com.vipycm.mao.camera.filter.ToneCurveFilter;

public class CameraActivity extends Activity {

    private MaoLog log = MaoLog.getLogger(getClass().getSimpleName());
    private CameraView mCameraView;
    private ImageView iv_record;
    private final String mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mao/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        iv_record = (ImageView) findViewById(R.id.iv_record);

        ToneCurveFilter toneCurveFilter = new ToneCurveFilter();
        toneCurveFilter.setFromCurveFileInputStream(MaoApp.getContext().getResources().openRawResource(R.raw.tone_cuver_sample));
        mCameraView.setFilter(toneCurveFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        mCameraView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.iv_switch:
                mCameraView.switchCamera();
                break;
            case R.id.iv_capture:
                log.i("capture start");
                mCameraView.capture(new ICaptureCallback() {
                    @Override
                    public void onCapture(Bitmap bitmap) {
                        String path = mSavePath + System.currentTimeMillis() + ".jpg";
                        FileUtils.saveImage(path, bitmap);
                        log.i("onCapture:" + path);
                    }
                });
                break;
            case R.id.iv_record:
                if (mCameraView.isRecording()) {
                    mCameraView.stopRecording();
                    iv_record.setColorFilter(0);
                } else {
                    String path = mSavePath + System.currentTimeMillis() + ".mp4";
                    mCameraView.startRecording(path);
                    iv_record.setColorFilter(Color.RED);
                }
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }
}
