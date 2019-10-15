package com.note.dao;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SQLManage extends Activity{

	/*
	 * 数据管理类
	 * 
	 */
	
	//执行插入数据的操作
	public void Insert(SQLiteDatabase sqlDB,String name,String content,String time)
	{
		ContentValues cv=new ContentValues();
		cv.put("noteName", name);
		cv.put("noteContent", content);
		cv.put("noteTime", time);
		sqlDB.insert("note", null, cv);
		sqlDB.close();
	}
	
	//执行数据修改更新的操作
	public void saveNote(SQLiteDatabase sdb,String name,String content,String noteId,String time){		
		ContentValues cv=new ContentValues();
		cv.put("noteName", name);
		cv.put("noteContent", content);
		cv.put("noteTime", time);
		sdb.update("note", cv, "noteId=?", new String[]{noteId});
		sdb.close();
	}
	
	//返回当前系统时间
	public String returnTime(){
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8:00"));//修改默认时区
		Date d=new Date(System.currentTimeMillis());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=sdf.format(d);
		return time;
	}
	
	//模糊查询
	public void blruSearch(SQLiteDatabase db,String keyStr)
	{
		String sql = "select * from note where noteName like '%"+keyStr+"%'";
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext())
		{
			
		}
	}
}
