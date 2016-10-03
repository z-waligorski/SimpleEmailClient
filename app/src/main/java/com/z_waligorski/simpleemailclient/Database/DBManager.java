package com.z_waligorski.simpleemailclient.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBManager extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "messages";
    public static final String ID_KEY = "_id";
    public static final String ADDRESS_KEY = "address";
    public static final String DATE_KEY = "date";
    public static final String SUBJECT_KEY = "subject";
    public static final String CONTENT_KEY = "content";
    public static final String NEW_KEY = "new";

    public DBManager(Context context) {
        super(context, "messages.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( "
                + ID_KEY + " INTEGER PRIMARY KEY,"
                + ADDRESS_KEY + " TEXT,"
                + DATE_KEY + " TEXT,"
                + SUBJECT_KEY + " TEXT,"
                + CONTENT_KEY + " TEXT,"
                + NEW_KEY + " TEXT"
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    // Insert new messages into table
    public void insertMessages(ArrayList<HashMap<String, String>> messageList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i = 0; i < messageList.size(); i++) {
            HashMap<String, String> messageMap = new HashMap<String, String>();
            ContentValues values = new ContentValues();
            messageMap = messageList.get(i);
            values.put(ADDRESS_KEY, messageMap.get("address"));
            values.put(DATE_KEY, messageMap.get("date"));
            values.put(SUBJECT_KEY, messageMap.get("subject"));
            values.put(CONTENT_KEY, messageMap.get("content"));
            values.put(NEW_KEY, messageMap.get("new"));

            db.insert( TABLE_NAME, null, values);
        }
        db.close();
    }

    // Get message from table
    public HashMap<String, String> getMessage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE _id = "+id;
        Cursor cursor = db.rawQuery(query, null);
        HashMap<String, String> map = new  HashMap<String, String>();
        if(cursor.moveToFirst()) {
            do {
                map.put(ADDRESS_KEY, cursor.getString(1));
                map.put(DATE_KEY, cursor.getString(2));
                map.put(SUBJECT_KEY, cursor.getString(3));
                map.put(CONTENT_KEY, cursor.getString(4));
                map.put(NEW_KEY, cursor.getString(5));
            } while (cursor.moveToNext());
        }
        db.close();
        return map;
    }

    public void markMessageAsRead(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET new = \"\" WHERE " + ID_KEY + " = " + id;
        db.execSQL(query);
    }

    // Get data needed for populating ListView
    public Cursor getAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_KEY + "," + ADDRESS_KEY + "," + DATE_KEY + "," +
                SUBJECT_KEY + "," + NEW_KEY + " FROM " + TABLE_NAME + " ORDER BY " + ID_KEY;
        return db.rawQuery(query, null);
    }

    // Delete all rows from table
    public void deleteMessages(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
