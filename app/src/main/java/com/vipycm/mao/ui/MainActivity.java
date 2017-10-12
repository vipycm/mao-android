package com.vipycm.mao.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.camera.CameraActivity;
import com.vipycm.mao.ui.MainFragment.OnMainFragmentInteraction;

import org.opencv.samples.facedetect.FdActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 * Created by mao on 16-12-29.
 */
public class MainActivity extends FragmentActivity implements OnMainFragmentInteraction {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    private static final List<FuncItem> FUNC_ITEMS = new ArrayList<>();
    private MaoFragment mCurrentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.i("onCreate");
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
    }

    @Override
    protected void onResume() {
        log.i("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        log.i("onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        log.i("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        log.i("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log.i("onDestroy");
        MaoLog.flushLog(false);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log.i("onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (mCurrentFragment != null) {
            mCurrentFragment.onMaoActivityResult(requestCode, resultCode, data);
        }
    }

    public void onMaoClick(View v) {
        if (mCurrentFragment != null) {
            mCurrentFragment.onMaoClick(v);
        }
    }

    @Override
    public void onFuncItemClicked(FuncItem item) {
        log.i("onFuncItemClicked " + item.name);

        //根据item决定跳转到哪个Fragment
        MaoFragment fragment = null;
        if (dbItem == item) {
            fragment = new DbFragment();

        } else if (helloJniItem == item) {
            fragment = new HelloJniFragment();

        } else if (pmItem == item) {
            fragment = new PmFragment();

        } else if (dsItem == item) {
            fragment = new DsFragment();

        } else if (billItem == item) {
            fragment = new BillingFragment();

        } else if (camera2Item == item) {
            fragment = new Camera2Fragment();

        } else if (cameraGLItem == item) {
            fragment = new CameraGLFragment();

        } else if (videoPlayItem == item) {
            fragment = new VideoPlayFragment();

        } else if (audioDecoderItem == item) {
            fragment = new AudioDecoderFragment();

        } else if (faceDetectionItem == item) {
            FdActivity.start(this);
            return;

        } else if (cameraItem == item) {
            CameraActivity.start(this);
            return;

        } else if (cameraNewItem == item) {
            com.vipycm.mao.cameranew.CameraActivity.start(this);
            return;

        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mCurrentFragment = fragment;
        }
    }

    @Override
    public List<FuncItem> getFuncItems() {
        return FUNC_ITEMS;
    }

    //定义功能项
    static final FuncItem dbItem = new FuncItem("db");
    static final FuncItem helloJniItem = new FuncItem("hello jni");
    static final FuncItem pmItem = new FuncItem("pm");
    static final FuncItem dsItem = new FuncItem("DS");
    static final FuncItem billItem = new FuncItem("billing");
    static final FuncItem camera2Item = new FuncItem("camera2");
    static final FuncItem cameraGLItem = new FuncItem("cameraGL");
    static final FuncItem faceDetectionItem = new FuncItem("face detection");
    static final FuncItem cameraItem = new FuncItem("camera");
    static final FuncItem cameraNewItem = new FuncItem("camera new");
    static final FuncItem videoPlayItem = new FuncItem("Video Play");
    static final FuncItem audioDecoderItem = new FuncItem("Audio Play");

    //将功能项加入到FUNC_ITEMS
    static {
        FUNC_ITEMS.add(dbItem);
        FUNC_ITEMS.add(helloJniItem);
        FUNC_ITEMS.add(pmItem);
        FUNC_ITEMS.add(dsItem);
        FUNC_ITEMS.add(billItem);
        FUNC_ITEMS.add(camera2Item);
        FUNC_ITEMS.add(cameraGLItem);
        FUNC_ITEMS.add(faceDetectionItem);
        FUNC_ITEMS.add(cameraItem);
        FUNC_ITEMS.add(cameraNewItem);
        FUNC_ITEMS.add(videoPlayItem);
        FUNC_ITEMS.add(audioDecoderItem);
    }

    public static class FuncItem {
        public String name;

        FuncItem(String name) {
            this.name = name;
        }
    }
}
