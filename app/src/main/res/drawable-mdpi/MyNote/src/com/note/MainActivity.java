package com.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.note.dao.SqliteDBConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
	private ListView listview;
	private SqliteDBConnect sqlConn;
	int count = 0;
	private EditText txtBlur = null;
	private Button btnAddNewText, btnAddNewVoice,btnAllNote,btnSearch;
	private SimpleAdapter adapter;
	private ActivityManager manage;
	private SQLiteDatabase db;
	private SqliteDBConnect connect;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setProgressBarVisibility(true);
		setContentView(R.layout.mainshow);
		connect = new SqliteDBConnect(getApplicationContext());
		db = connect.getReadableDatabase();
		
		manage = ActivityManager.getInstance();
		manage.addActivity(this);

		ButtonClick click = new ButtonClick();
		btnAddNewText = (Button)this.findViewById(R.id.btnAddNewText);
		btnAddNewVoice = (Button)this.findViewById(R.id.btnAddNewVoice);
		btnSearch = (Button)this.findViewById(R.id.btnSearch);
		btnAllNote = (Button)this.findViewById(R.id.btnAll);
		
		btnSearch.setOnClickListener(click);
		btnAllNote.setOnClickListener(click);
		btnAddNewText.setOnClickListener(click);
		btnAddNewVoice.setOnClickListener(click);

		//关键字查询搜索的文本输入框
		txtBlur = (EditText)this.findViewById(R.id.search_Content);
		
		listview = (ListView) findViewById(R.id.listview);

		sqlConn = new SqliteDBConnect(MainActivity.this);

		disPlay();//从数据库提取数据并显示
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) arg0.getItemAtPosition(arg2);
				Intent intent = new Intent();
				intent.putExtra("noteId", map.get("noteId").toString());
				intent.setClass(MainActivity.this, DisPlay.class);
				startActivity(intent);
			}
		});
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {	
				@SuppressWarnings("unchecked")
				final Map<String, Object> map = (Map<String, Object>) arg0
						.getItemAtPosition(arg2);
				AlertDialog.Builder adb = new Builder(MainActivity.this);
				adb.setTitle(map.get("noteName").toString());
				adb.setItems(new String[] { "删 除", "修 改" },
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									SQLiteDatabase sdb = sqlConn.getReadableDatabase();
									sdb.delete("note", "noteId=?",new String[] { map.get("noteId").toString() });
									Toast.makeText(MainActivity.this, "删除成功",Toast.LENGTH_SHORT).show();
									sdb.close();
									disPlay();
									break;
								case 1:
									Intent intent = new Intent();
									intent.putExtra("noteId", map.get("noteId").toString());
									intent.setClass(MainActivity.this, DisPlay.class);
									startActivity(intent);
									break;
								}
							}
						});
				adb.show();
				return false;
			}
		});

	}

	public void disPlay() {
		SQLiteDatabase sdb = sqlConn.getReadableDatabase();
		count = 0;
		Cursor c1 = sdb.query("note", new String[] { "noteId", "noteName",
				"noteTime" }, null, null, null, null, "noteId asc");
		while (c1.moveToNext()) {
			int noteid = c1.getInt(c1.getColumnIndex("noteId"));
			if (noteid > count)
				count = noteid;
		}
		c1.close();

		Cursor c = sdb.query("note", new String[] { "noteId", "noteName",
		"noteTime" }, null, null,
				null, null, "noteId asc");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		while (c.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			String strName = c.getString(c.getColumnIndex("noteName"));
			if (strName.length() > 6) {
				map.put("noteName", strName.substring(0, 6) + "…");
			} else {
				map.put("noteName", strName);
			}
			map.put("noteTime", c.getString(c.getColumnIndex("noteTime")));
			map.put("noteId", c.getInt(c.getColumnIndex("noteId")));
			list.add(map);
		}
		c.close();
		sdb.close();
		if (count > 0) {
			adapter = new SimpleAdapter(MainActivity.this, list, R.layout.items,
					new String[] { "noteName", "noteTime" }, new int[] {
							R.id.noteName, R.id.noteTime });
			listview.setAdapter(adapter);
		}

	}

	//菜单按钮
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "设置铃声");
		menu.add(0, 2, 2, "退出");
		return super.onCreateOptionsMenu(menu);
	}

	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent=new Intent();
			intent.setClass(MainActivity.this,SetAlarm.class);
			startActivity(intent);
			break;
		case 2:
			AlertDialog.Builder adb2 = new Builder(MainActivity.this);
			adb2.setTitle("消息");
			adb2.setMessage("真的要退出吗？");
			adb2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					manage.exitAllProgress();
				}
			});
			adb2.setNegativeButton("取消", null);
			adb2.show();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	
	//手机的返回(后退)按钮的事件处理
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder adb = new Builder(MainActivity.this);
			adb.setTitle("消息");
			adb.setMessage("确认退出？");
			adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					manage.exitAllProgress();
				}
			});
			adb.setNegativeButton("取消", null);
			adb.show();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	class ButtonClick implements View.OnClickListener
	{

		private static final String TAG = "XU";

		public void onClick(View v) {
			if(v.getId()==R.id.btnAddNewText)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AddNewText.class);
				startActivity(intent);
			}
			else if(v.getId()==R.id.btnAddNewVoice)
			{
				Intent intent2 = new Intent();
				intent2.setClass(MainActivity.this, AddNewVoice.class);
				startActivity(intent2);
			}
			
			//搜索按钮的事件处理
			else if(v.getId() == R.id.btnSearch)
			{
				String keyStr = txtBlur.getText().toString().trim();
				//模糊查询的语句
				String sql = "select * from note where noteName like '%"+keyStr+"%'";
				Log.i(TAG, "sql="+sql);
				Cursor cursor = db.rawQuery(sql, null);
				List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
				while(cursor.moveToNext())
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					String name = cursor.getString(cursor.getColumnIndex("noteName"));
					String time = cursor.getString(cursor.getColumnIndex("noteTime"));
					int id = cursor.getInt(cursor.getColumnIndex("noteId"));
					map.put("id", id);
					map.put("noteName", name);
					map.put("noteTime", time);
					list.add(map);
				}
				adapter = new SimpleAdapter(MainActivity.this, list, R.layout.items,
						new String[] { "id","noteName", "noteTime" }, new int[] {
								R.id.noteId,R.id.noteName, R.id.noteTime });
				listview.setAdapter(adapter);
				txtBlur.setText("");
			}
			else if(v.getId() == R.id.btnAll)
			{
				disPlay();
				txtBlur.setText("");
			}
		}
		
	}
}
