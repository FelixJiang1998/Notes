package com.note.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDBConnect extends SQLiteOpenHelper {
    public SqliteDBConnect(Context context) {
        super(context, "Notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table note(noteId Integer primary key,noteName varchar(20),noteTime varchar(20),noteContent varchar(400),category Integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
