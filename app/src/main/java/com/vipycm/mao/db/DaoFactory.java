package com.vipycm.mao.db;

/**
 * Created by mao on 2016/5/5.
 */
public class DaoFactory {

    private static UserDao mUserDao;
    private static final Object mUserDaoLock = new Object();

    public static UserDao getUserDao() {
        if (mUserDao == null) {
            synchronized (mUserDaoLock) {
                if (mUserDao == null) {
                    mUserDao = new UserDao();
                }
            }
        }
        return mUserDao;
    }
}
