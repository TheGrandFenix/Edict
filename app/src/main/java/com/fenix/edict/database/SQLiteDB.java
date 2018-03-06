package com.fenix.edict.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EdictDatabase.db";

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MESSAGES (MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT, MESSAGE_SERVER_ID INTEGER NOT NULL, TIMESTAMP INTEGER NOT NULL DEFAULT ((strftime('%s', 'now'))), SENDER_ID NOT NULL, DIRECT_MESSAGE BOOLEAN DEFAULT 1 NOT NULL, TARGET_ID INTEGER NOT NULL, CONTENT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS MESSAGES");
        onCreate(db);
    }
}
