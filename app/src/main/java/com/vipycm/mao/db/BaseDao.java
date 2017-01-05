package com.vipycm.mao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 2016/5/5.
 */
public abstract class BaseDao<T> {

    private String mTableName;

    protected BaseDao(String tableName) {
        mTableName = tableName;
    }

    protected abstract void onCreate(SQLiteDatabase database);

    protected abstract void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);

    protected SQLiteDatabase getWritableDatabase() {
        return MaoDbHelper.getInstance().getWritableDatabase();
    }

    protected SQLiteDatabase getReadableDatabase() {
        return MaoDbHelper.getInstance().getReadableDatabase();
    }

    protected abstract ContentValues getContentValues(T t);

    protected abstract T findByCursor(Cursor cursor);

    public boolean add(T t) {
        try {
            return -1 != getWritableDatabase().insert(mTableName, null, getContentValues(t));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean update(T t, String whereClause, String[] whereArgs) {
        try {
            return 0 < getWritableDatabase().update(mTableName, getContentValues(t), whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected boolean delete(String whereClause, String[] whereArgs) {
        try {
            return 0 < getWritableDatabase().delete(mTableName, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    protected List<T> findAll(String orderBy, String... rows) {
        List<T> result = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(mTableName, rows, null, null, null, null, orderBy);
        if (cursor == null) {
            return result;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T t = findByCursor(cursor);
            if (t != null) {
                result.add(t);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }
}
