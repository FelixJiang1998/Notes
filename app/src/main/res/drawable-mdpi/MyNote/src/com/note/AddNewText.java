package com.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.note.dao.SQLManage;
import com.note.dao.SqliteDBConnect;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class AddNewText extends Activity implements OnClickListener {
	
	/*
	 * 添加新的文本笔记备忘的页面
	 */
	private static final String TAG = "XU";
	Button btnSave,btnClean,btnReturn,btnExit,btnGetTime; 
	EditText txtName,txtContent; //名字、内容的输入框
	TextView timeView;
	String name,content,time;
	
	SimpleDateFormat format;
	Date date;
	SQLiteDatabase sqlDB;
	ActivityManager manager;
	SQLManage sqlManage;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_text);
		
		manager = ActivityManager.getInstance();
		manager.addActivity(this);
		
		sqlManage = new SQLManage();
		timeView = (TextView)this.findViewById(R.id.noteTime);
		timeView.setText(manager.returnTime());
		timeView.setTextColor(Color.RED);
		timeView.setBackgroundColor(Color.WHITE);

		
		btnSave = (Button)this.findViewById(R.id.btnSave);
		btnReturn = (Button)this.findViewById(R.id.btnReturn);
		btnExit = (Button)this.findViewById(R.id.btnExit2);
		btnClean = (Button)this.findViewById(R.id.btnClean);
		btnGetTime = (Button)this.findViewById(R.id.btngGetnoteTime);
		
		btnSave.setOnClickListener(this);
		btnReturn.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		btnClean.setOnClickListener(this);
		btnGetTime.setOnClickListener(this);
		
		txtName = (EditText)this.findViewById(R.id.note_name);
		txtContent = (EditText)this.findViewById(R.id.note_content);
		
		SqliteDBConnect sd = new SqliteDBConnect(AddNewText.this);
		sqlDB = sd.getReadableDatabase();
	}

	public void onClick(View v) {
		name = txtName.getText().toString().trim();
		content = txtContent.getText().toString().trim();
		time = sqlManage.returnTime();

		if(v.getId()==R.id.btnSave)
		{
			if(name.equals(""))
			{
				Toast.makeText(AddNewText.this, "名字不允许为空", Toast.LENGTH_SHORT).show();
			}
			else 
			{
				save();
			}
		}
		
		//清空按钮事件
		else if(v.getId()==R.id.btnClean)
		{
			txtName.setText("");
			txtContent.setText("");
		}
		
		//返回按钮事件
		else if(v.getId()==R.id.btnReturn)
		{
			Log.i(TAG, "返回按钮");
			Intent intent = new Intent(AddNewText.this, MainActivity.class);
			startActivity(intent);
			this.finish();
		}
		
		//退出按钮的事件，用提示对话框进行确认
		else if(v.getId()==R.id.btnExit2)
		{
			Log.i(TAG, "退出按钮");
			AlertDialog.Builder builder = new Builder(AddNewText.this);
			builder.setMessage("确认要退出吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					manager.exitAllProgress();
				}
			});
			
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			builder.show();
			

		}
		
		else if(v.getId() == R.id.btngGetnoteTime)
		{
			Log.i(TAG, "获取时间按钮");
			timeView.setText(manager.returnTime());
		}
	}
	
	private void save() {
		sqlManage.Insert(sqlDB, name, content, time);//插入数据
		Intent intent2 = new Intent();
		intent2.setClass(this, MainActivity.class);
		startActivity(intent2);
	}

		//菜单按钮
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, 1, 1, "关于");
			menu.add(0, 2, 2, "设置闹铃声");
			menu.add(0, 3, 3, "退出");
			return super.onCreateOptionsMenu(menu);
		}

		//菜单按钮选择事件
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
			switch (item.getItemId()) {
			case 1:
				AlertDialog.Builder adb = new Builder(AddNewText.this);
				adb.setTitle("关于");
				adb.setMessage("泉州信息工程学院\n\t 移动App团队制作");
				adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(AddNewText.this, "Thanks",
								Toast.LENGTH_SHORT).show();
					}
				});
				adb.show();
				break;
			case 2:
				Intent intent = new Intent();
				intent.setClass(AddNewText.this, SetAlarm.class);
				startActivity(intent);
				break;
			case 3:
				AlertDialog.Builder adb2 = new Builder(AddNewText.this);
				adb2.setTitle("消息");
				adb2.setMessage("真的要退出吗？");
				adb2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						manager.exitAllProgress();
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

		
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			//返回按钮的事件
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				AlertDialog.Builder adb = new Builder(AddNewText.this);
				adb.setTitle("消息");
				adb.setMessage("是否要保存？");
				adb.setPositiveButton("保存", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						if(!(txtName.getText().toString().equals("")))
						{
							save();
							Intent intent2 = new Intent();
							intent2.setClass(AddNewText.this, MainActivity.class);
							startActivity(intent2);
						}
						else 
						{
							Toast.makeText(AddNewText.this, "请先输入名称", Toast.LENGTH_SHORT).show();
						}
					}
				});
				adb.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent2 = new Intent();
						intent2.setClass(AddNewText.this, MainActivity.class);
						startActivity(intent2);
					}
				});
				adb.show();
			}
			return super.onKeyDown(keyCode, event);
		}
}
	
	

