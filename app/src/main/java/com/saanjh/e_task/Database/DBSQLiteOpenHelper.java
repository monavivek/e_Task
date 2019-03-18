package com.saanjh.e_task.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBSQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    private static final String DB_NAME = "taskmanager.db";
    private static final int VERSION = 5;
    private static final String TABLE_REGISTER = "register";
    private static final String TABLE_LASTVISIT = "lastvisit";
    private SQLiteDatabase db;
    private ContentValues values;
    private Cursor cursor;

    public DBSQLiteOpenHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REGISTER + " (u_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,NAME TEXT NULL,MOBILE TEXT,EMAILID TEXT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_LASTVISIT + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,MOBILENO TEXT,LASTVISIT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addregister(String[] col_value) {
        db = getWritableDatabase();
        values = new ContentValues();
        values.put("NAME", col_value[0].toString());
        values.put("MOBILE", col_value[1].toString());
        values.put("EMAILID", col_value[2].toString());
        db.insert(TABLE_REGISTER, null, values);
        db.close();
    }

    public void addlastvisit(String[] col_values) {
        db = getWritableDatabase();
        values = new ContentValues();
        values.put("MOBILENO", col_values[0].toString());
        values.put("LASTVISIT", col_values[1].toString());
        db.insert(TABLE_LASTVISIT, null, values);
        db.close();
    }

    public String getlastvisit(String mobile) {
        String lst = "";
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT LASTVISIT FROM " + TABLE_LASTVISIT + " WHERE MOBILENO='" + mobile + "' ORDER BY id desc limit 1", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    lst = cursor.getString(0);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();
        db.close();
        return lst;
    }

    public String getUser(String mobno) {
        String str1 = "";
        db = getReadableDatabase();
        cursor = db.rawQuery("SELECT MOBILE FROM " + TABLE_REGISTER + " WHERE MOBILE='" + mobno + "' ORDER BY u_id desc limit 1", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    str1 = cursor.getString(0);
                    cursor.moveToNext();
                }
            }
        }
        return str1;
    }
}