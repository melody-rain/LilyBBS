package com.os.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jin on 2014/9/10.
 */
public
class DatabaseDealer {

    private static final String DEFAULT_DATABASE_NAME = "LILY";

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
}
