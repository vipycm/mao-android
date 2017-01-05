package com.vipycm.mao.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.jni.HelloJni;

/**
 * HelloJniFragment
 * Created by mao on 2016/12/29.
 */
public class HelloJniFragment extends Fragment implements View.OnClickListener {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());

    TextView txt_content;
    Button btn_ok;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log.i("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_sample, container, false);
        txt_content = (TextView) rootView.findViewById(R.id.txt_content);
        txt_content.setText(this.getClass().getSimpleName());
        btn_ok = (Button) rootView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        log.i("onDestroyView");
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                txt_content.setText(HelloJni.stringFromJNI());
                break;
        }
    }
}
