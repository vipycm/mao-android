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
import com.vipycm.mao.db.DaoFactory;
import com.vipycm.mao.db.UserDao;
import com.vipycm.mao.model.User;

import java.util.List;
import java.util.Random;

/**
 * DbFragment
 * Created by mao on 2016/5/5.
 */
public class DbFragment extends Fragment implements View.OnClickListener {

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
                UserDao userDao = DaoFactory.getUserDao();
                User u = new User();
                u.setId(String.valueOf(System.currentTimeMillis()));
                u.setName(u.getId());
                u.setSex("M");
                u.setAge(new Random().nextInt(100));
                log.i(u.toString());
                //add
                userDao.add(u);
                //delete
                if (u.getAge() > 50) {
                    userDao.delete(u);
                }
                //update
                u.setName(u.getSex() + u.getAge());
                userDao.update(u);
                //find
                List<User> users = userDao.findAll();
                txt_content.setText("user count:" + users.size());
                log.i(users.toString());
        }
    }
}
