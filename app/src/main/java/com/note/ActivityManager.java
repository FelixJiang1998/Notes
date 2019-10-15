package com.note;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ActivityManager {
    private static final String TAG = "XU";
    private static ActivityManager instance;
    private static Uri uri = Uri.parse("/sdcard/mp3.mp3");
    private List<Activity> list;

    public static ActivityManager getInstance() {
        if (instance == null)
            instance = new ActivityManager();
        return instance;
    }

    public static Uri getUri() {
        return uri;
    }

    public static void setUri(Uri uri) {
        ActivityManager.uri = uri;
    }

    public void addActivity(Activity av) {
        if (list == null)
            list = new ArrayList<Activity>();
        if (av != null) {
            list.add(av);
        }
    }

    public void exitAllProgress() {
        for (int i = 0; i < list.size(); i++) {
            Activity av = list.get(i);
            av.finish();
        }
    }

    //save
    public void saveNote(SQLiteDatabase sdb, String name, String content, String noteId, String time, int category) {
        ContentValues cv = new ContentValues();
        cv.put("noteName", name);
        cv.put("noteContent", content);
        cv.put("noteTime", time);
        cv.put("category", category);
        Log.i(TAG, "添加" + cv.get("noteName").toString());
        sdb.update("note", cv, "noteId=?", new String[]{noteId});
        sdb.close();
    }

    //insert
    public void addNote(SQLiteDatabase sdb, String name, String content, String time, int category) {
        Log.i(TAG, "3");
        ContentValues cv = new ContentValues();
        cv.put("noteName", name);
        cv.put("noteContent", content);
        cv.put("noteTime", time);
        cv.put("category", category);
        sdb.insert("note", null, cv);
        sdb.close();
        Log.i(TAG, "4");
    }

    public String returnTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));//修改默认时区
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        return time;
    }
}
