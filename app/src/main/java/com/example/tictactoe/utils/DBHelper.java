package com.example.tictactoe.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.PreparedStatement;

public class DBHelper extends SQLiteOpenHelper{

    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, "login.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("create Table users(username TEXT primary key, password TEXT, email TEXT, recoveryQuestion TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
    }

    public Boolean insertData(String username, String password, String email, String recoveryQuestion) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("recoveryquestion", recoveryQuestion);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }

    public Boolean checkusername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ?", new String[] {username});
        return cursor.getCount() > 0;
    }

    public Boolean checkusernamepassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and password = ?", new String[] {username, password});
        return cursor.getCount() > 0;
    }

    public  Boolean accountrecovery(String username, String email, String recoveryQuestion) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("Select * from users where username = ? and email = ? and recoveryQuestion = ?", new String[] {username, email, recoveryQuestion});
        return cursor.getCount() > 0;
    }

    public Boolean updatepassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", password);
        long result = MyDB.update("users", contentValues, "username = ?", new String[] {username});
        return result != -1;
    }

}
