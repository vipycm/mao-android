package com.vipycm.mao.ui;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentSampleBinding;

import java.lang.reflect.Method;

/**
 * PmFragment
 * Created by mao on 2017/1/16.
 */
public class PmFragment extends MaoFragment {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    private FragmentSampleBinding mDataBinding;
    private Handler mHandler = null;

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
                getPackageInfo(getContext(), getContext().getPackageName());
                break;
        }
    }

    private void getPackageInfo(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        try {
            Method method_getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method_getPackageSizeInfo.invoke(pm, pkg, new IPackageStatsObserver.Stub() {

                @Override
                public IBinder asBinder() {
                    log.d("asBinder");
                    return super.asBinder();
                }

                @Override
                public void onGetStatsCompleted(PackageStats packageStats, boolean b) throws RemoteException {
                    final StringBuilder sb = new StringBuilder("onGetStatsCompleted\n");
                    sb.append("packageName:").append(packageStats.packageName).append("\n");
                    sb.append("cacheSize:").append(packageStats.cacheSize).append("\n");
                    sb.append("dataSize:").append(packageStats.dataSize).append("\n");
                    sb.append("externalDataSize:").append(packageStats.externalDataSize).append("\n");
                    log.i(sb.toString());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDataBinding.txtContent.setText(sb);
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
