package com.vipycm.mao.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vipycm.mao.MaoApp;
import com.vipycm.mao.R;
import com.vipycm.mao.camera.filter.CameraFilterGroup;
import com.vipycm.mao.camera.filter.ToneCurveFilter;

public class CameraActivity extends Activity {

    private CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraView = (CameraView) findViewById(R.id.camera_view);

        ToneCurveFilter toneCurveFilter = new ToneCurveFilter();
        toneCurveFilter.setFromCurveFileInputStream(MaoApp.getContext().getResources().openRawResource(R.raw.tone_cuver_sample));
        CameraFilterGroup filterGroup = new CameraFilterGroup();
        filterGroup.addFilter(toneCurveFilter);

        mCameraView.setFilter(filterGroup);
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
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }
}
