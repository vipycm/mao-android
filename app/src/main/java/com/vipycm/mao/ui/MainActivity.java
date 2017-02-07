package com.vipycm.mao.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.vipycm.commons.MaoLog;
import com.vipycm.mao.R;
import com.vipycm.mao.ui.MainFragment.OnMainFragmentInteraction;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity
 * Created by mao on 16-12-29.
 */
public class MainActivity extends FragmentActivity implements OnMainFragmentInteraction {

    private MaoLog log = MaoLog.getLogger(this.getClass().getSimpleName());
    private static final List<FuncItem> FUNC_ITEMS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }
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
    public void onFuncItemClicked(FuncItem item) {
        log.i("onFuncItemClicked " + item.name);

        //根据item决定跳转到哪个Fragment
        Fragment fragment = null;
        if (dbItem == item) {
            fragment = new DbFragment();

        } else if (helloJniItem == item) {
            fragment = new HelloJniFragment();

        } else if (pmItem == item) {
            fragment = new PmFragment();

        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
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

    //将功能项加入到FUNC_ITEMS
    static {
        FUNC_ITEMS.add(dbItem);
        FUNC_ITEMS.add(helloJniItem);
        FUNC_ITEMS.add(pmItem);
    }

    public static class FuncItem {
        public String name;

        FuncItem(String name) {
            this.name = name;
        }
    }
}
