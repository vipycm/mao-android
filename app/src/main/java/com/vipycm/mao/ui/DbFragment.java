package com.vipycm.mao.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.databinding.FragmentSampleBinding;
import com.vipycm.mao.db.DaoFactory;
import com.vipycm.mao.db.UserDao;
import com.vipycm.mao.model.User;

import java.util.List;
import java.util.Random;

/**
 * DbFragment
 * Created by mao on 2016/5/5.
 */
public class DbFragment extends MaoFragment {

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
                mDataBinding.txtContent.setText("user count:" + users.size());
                log.i(users.toString());
        }
    }
}
