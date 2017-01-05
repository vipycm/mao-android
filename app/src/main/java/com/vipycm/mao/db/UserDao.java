package com.vipycm.mao.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.vipycm.mao.model.User;

import java.util.List;

/**
 * Created by mao on 2016/5/5.
 */
public class UserDao extends BaseDao<User> {

    public static final String TABLE_NAME = "user";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SEX = "sex";
    private static final String COLUMN_AGE = "age";

    public UserDao() {
        super(TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME).append("(")
                .append(COLUMN_ID + " TEXT PRIMARY KEY,")
                .append(COLUMN_NAME + " TEXT,")
                .append(COLUMN_SEX + " TEXT,")
                .append(COLUMN_AGE + " INTEGER")
                .append(")");

        database.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        onCreate(database);
    }

    @Override
    protected User findByCursor(Cursor cursor) {
        try {
            User info = new User();
            info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            info.setSex(cursor.getString(cursor.getColumnIndex(COLUMN_SEX)));
            info.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ContentValues getContentValues(User user) {
        if (user == null || TextUtils.isEmpty(user.getId())) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.getId());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_SEX, user.getSex());
        values.put(COLUMN_AGE, user.getAge());
        return values;
    }

    public boolean update(User info) {
        return super.update(info, COLUMN_ID + "=?", new String[]{info.getId()});
    }

    public boolean delete(User info) {
        return super.delete(COLUMN_ID + "=?", new String[]{info.getId()});
    }

    public List<User> findAll() {
        return super.findAll(null, COLUMN_ID, COLUMN_NAME, COLUMN_SEX, COLUMN_AGE);
    }
}
