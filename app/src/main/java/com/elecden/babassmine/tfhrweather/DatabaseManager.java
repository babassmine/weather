package com.elecden.babassmine.tfhrweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by babassmine on 2/20/15.
 */

public class DatabaseManager {

    public static final String DB_NAME = "forecast.db";
    public static final String TABLE_NAME = "forecast";
    public static final int DB_VERSION = 1;

    private static final String DB_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + "("+
            "time INTEGER PRIMARY KEY,"+
            "resp TEXT)";

    private SQLHelper helper;
    private SQLiteDatabase db;
    private Context context;

    public DatabaseManager(Context c){
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
       // this.db.execSQL("DROP TABLE");
    }

    public DatabaseManager openReadable() throws SQLException{
        helper = new SQLHelper(context);
        db = helper.getReadableDatabase();
        return this;
    }

    public void close(){
        db.close();
    }

    public void addRow(int hour, String resp){
        ContentValues new_entry = new ContentValues();
        new_entry.put("time", hour);
        new_entry.put("resp", resp);

        try{
            db.insertOrThrow(TABLE_NAME, null, new_entry);
        }catch (Exception e){
            Log.e("Error in inserting row", e.toString());
            e.printStackTrace();
        }

    }

    public String retrieveRows(){
        String[] columns = new String[]{"time", "resp"};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        String table_rows = "";
        cursor.moveToFirst();
        table_rows = cursor.getString(0)+ "," + cursor.getString(1);

        if (cursor != null && !cursor.isClosed()){
            cursor.close();
        }

        return table_rows;
    }

    public class SQLHelper extends SQLiteOpenHelper{

        public SQLHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(SQLHelper.class.getName(),
                    "Upgrading database from version , which will destroy all old data");
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(SQLHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
