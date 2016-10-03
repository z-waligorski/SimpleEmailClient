package com.z_waligorski.simpleemailclient.UI;


import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.z_waligorski.simpleemailclient.Database.DBManager;

// This class is used to refresh UI after downloading new messages from server
public class RefreshUI {

    private DBManager dbManager;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    public RefreshUI(DBManager dbManager, Cursor cursor, SimpleCursorAdapter adapter) {
        this.dbManager = dbManager;
        this.cursor = cursor;
        this.adapter = adapter;
    }

    // Method that should be called in onPostExecute method of AsyncTask to refresh ListView
    public void refresh(){
        cursor = dbManager.getAll();
        adapter.swapCursor(cursor);
    }
}
