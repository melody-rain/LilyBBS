package com.os.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jin on 2014/9/10.
 */
public
class DatabaseDealer {

    private static final String DEFAULT_DATABASE_NAME = "LILY";

    private static final SQLiteDatabase createDataBase(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context, DEFAULT_DATABASE_NAME);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        return database;
    }

    private static final SQLiteDatabase createDatabase(Context context){
        DatabaseHelper dbHelper = new DatabaseHelper(context, DEFAULT_DATABASE_NAME);
        return dbHelper.getReadableDatabase();
    }

    public static final List<String> getBlockList(Context context){
        SQLiteDatabase db = createDatabase(context);
        Cursor cursor = db.rawQuery("select name from block", null);
        List<String> blockList = new ArrayList<String>();
        while (cursor.moveToNext()){
            blockList.add(cursor.getString(0));
        }

        cursor.close();
        db.close();
        return blockList;
    }

    public static final Bundle query(Context context) {
        Bundle bundle = new Bundle();
        SQLiteDatabase db = createDataBase(context);
        Cursor cursor = db.rawQuery("select username,password from userinfo order by _id desc",null);
        while(cursor.moveToNext()){
            bundle.putString("username", cursor.getString(0));
            bundle.putString("password", cursor.getString(1));
        }
        cursor.close();
        db.close();
        return bundle;
    }

//    public static final com.jerry.model.Settings getSettings(Context context) {
//        SQLiteDatabase db = createDataBase(context);
//        Bundle bundle = new Bundle();
//        Cursor cursor = db.rawQuery("select key,value from settings",null);
//        while(cursor.moveToNext()){
//            bundle.putString(cursor.getString(0), cursor.getString(1));
//        }
//        cursor.close();
//        db.close();
//        com.jerry.model.Settings settings = new com.jerry.model.Settings();
//        settings.setLogin(bundle.getString("isLogin").equals("1"));
//        settings.setSavePic(bundle.getString("isSavePic").equals("1"));
//        settings.setShowPic(bundle.getString("isShowPic").equals("1"));
//        settings.setSendMail(bundle.getString("isSendMail").equals("1"));
//        settings.setSign(bundle.getString("sign"));
//        return settings;
//    }
}
