package com.vipycm.mao.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentSampleBinding;
import com.vipycm.mao.jni.HelloJni;

/**
 * HelloJniFragment
 * Created by mao on 2016/12/29.
 */
public class HelloJniFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentSampleBinding mDataBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sample, container, false);
        mDataBinding.setHandler(this);
        mDataBinding.txtContent.setText(this.getClass().getSimpleName());
        return mDataBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onMaoClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                mDataBinding.txtContent.setText(HelloJni.stringFromJNI());
                break;
        }
    }
}
