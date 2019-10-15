package com.note;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.note.dao.SqliteDBConnect;

import java.util.Calendar;

public class ActivityMain extends Activity {
	private EditText txtName;
	private EditText txtContent, txtTime;
	private Button btnCommit;
	private Button btnCancel;
	private SQLiteDatabase sqlDB;
	private ActivityManager manage;

	private int year, month, day, hours, minute, second;
	private Calendar calendar;
	private PendingIntent pendingIntent;
	private AlarmManager alarmManage;

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		manage = ActivityManager.getInstance();
		manage.addActivity(this);

		txtName = (EditText) findViewById(R.id.noteName);
		txtContent = (EditText) findViewById(R.id.noteMain);
		btnCommit = (Button) findViewById(R.id.btnCommit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtTime = (EditText) findViewById(R.id.noteTime);
		SqliteDBConnect sd = new SqliteDBConnect(ActivityMain.this);
		sqlDB = sd.getReadableDatabase();

		txtTime.setText(manage.returnTime());
		txtTime.setTextColor(Color.RED);
		txtTime.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				calendar = Calendar.getInstance();
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH);
				day = calendar.get(Calendar.DAY_OF_MONTH);
				hours = calendar.get(Calendar.HOUR);
				minute = calendar.get(Calendar.MINUTE);
				second = calendar.get(Calendar.SECOND);
				DatePickerDialog dpd = new DatePickerDialog(ActivityMain.this,
						new DatePickerDialog.OnDateSetListener() {
							
							public void onDateSet(DatePicker view, int y,
									int monthOfYear, int dayOfMonth) {
								String[] time = { "",
										hours + ":" + minute + ":" + second };
								try {
									String[] time2 = txtTime.getText()
											.toString().trim().split(" ");
									if (time2.length == 2) {
										time[1] = time2[1];
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								String mo = "", da = "";
								if (monthOfYear < 10) {
									mo = "0" + (monthOfYear + 1);
								} else {
									mo = monthOfYear + "";
								}
								if (dayOfMonth < 10) {
									da = "0" + dayOfMonth;
								} else {
									da = dayOfMonth + "";
								}
								txtTime.setText(y + "-" + mo + "-" + da + " "
										+ time[1]);
							}
						}, year, month, day);
				dpd.setTitle("设置日期");
				dpd.show();
			}
		});
		txtTime.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				calendar = Calendar.getInstance();
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH);
				day = calendar.get(Calendar.DAY_OF_MONTH);
				hours = calendar.get(Calendar.HOUR);
				minute = calendar.get(Calendar.MINUTE);
				second = calendar.get(Calendar.SECOND);
				TimePickerDialog tpd = new TimePickerDialog(ActivityMain.this,
						new OnTimeSetListener() {
							
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								String[] time = {
										year + "-" + month + "-" + day, "" };
								try {
									time = txtTime.getText().toString().trim()
											.split(" ");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String ho = "", mi = "";
								if (hourOfDay < 10) {
									ho = "0" + hourOfDay;
								} else {
									ho = hourOfDay + "";
								}
								if (minute < 10) {
									mi = "0" + minute;
								} else {
									mi = minute + "";
								}
								txtTime.setText(time[0] + " " + ho + ":" + mi);
							}
						}, hours, minute, true);
				tpd.setTitle("设置时间");
				tpd.show();
				return true;
			}
		});
		btnCommit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int n = testTime();
				if (n == 0) {
					return;
				}
				AlertDialog.Builder adb = new Builder(ActivityMain.this);
				adb.setTitle("保存");
				adb.setMessage("Are you sure to Save?");
				adb.setPositiveButton("保存",
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog,
									int which) {
								baocunNote();

							}
						});
				adb.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(ActivityMain.this, "Don't Save",
										Toast.LENGTH_SHORT).show();
							}
						});
				adb.show();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder adb = new Builder(ActivityMain.this);
				adb.setTitle("提示");
				adb.setMessage("是否要保存？");
				adb.setPositiveButton("确定",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setClass(ActivityMain.this,MainActivity.class);
								startActivity(intent);
							}
						});
				adb.setNegativeButton("取消", null);
				adb.show();
			}
		});

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
			AlertDialog.Builder adb = new Builder(ActivityMain.this);
			adb.setTitle("关于");
			adb.setMessage("泉州信息工程学院\n\t 移动App团队制作");
			adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(ActivityMain.this, "Thanks",
							Toast.LENGTH_SHORT).show();
				}
			});
			adb.show();
			break;
		case 2:
			Intent intent = new Intent();
			intent.setClass(ActivityMain.this, SetAlarm.class);
			startActivity(intent);
			break;
		case 3:
			AlertDialog.Builder adb2 = new Builder(ActivityMain.this);
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

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		//返回按钮的事件
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder adb = new Builder(ActivityMain.this);
			adb.setTitle("消息");
			adb.setMessage("是否要保存？");
			adb.setPositiveButton("保存", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					int n = testTime();
					if (n == 0) {
						return;
					}
					baocunNote();
				}
			});
			adb.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent2 = new Intent();
					intent2.setClass(ActivityMain.this, MainActivity.class);
					startActivity(intent2);
				}
			});
			adb.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	public int testTime() {
		try {
			int flag = 0;
			String[] t = txtTime.getText().toString().trim().split(" ");
			String[] t1 = t[0].split("-");
			String[] t2 = t[1].split(":");
			if (t1.length != 3)
				flag = 1;
			if (t2.length != 2 && t2.length != 3)
				flag = 1;
			Integer.parseInt(t1[0]);
			int m = Integer.parseInt(t1[1]) - 1;
			int d = Integer.parseInt(t1[2]);
			int h = Integer.parseInt(t2[0]);
			int mi = Integer.parseInt(t2[1]);
			if (m < 0 || m > 11)
				flag = 1;
			if (d < 1 || d > 31)
				flag = 1;
			if (h < 0 || h > 24)
				flag = 1;
			if (mi < 0 || mi > 60)
				flag = 1;
			if (flag == 1) {
				Toast.makeText(ActivityMain.this, "时间格式不正确，请单击或长按时间框设置",
						Toast.LENGTH_SHORT).show();
				return 0;
			}

		} catch (Exception e) {
			Toast.makeText(ActivityMain.this, "日期时间不正确，请单击或长按时间框设置",
					Toast.LENGTH_SHORT).show();
			return 0;
		}
		return 1;
	}

	public void baocunNote() {
		String name = txtName.getText().toString().trim();
		String content = txtContent.getText().toString().trim();
		String time = txtTime.getText().toString().trim();
		if ("".equals(name) || "".equals(content)) {
			Toast.makeText(this, "名称和内容都不能为空", Toast.LENGTH_SHORT)
					.show();
		} else if ("请输入记事名称".equals(name) || "请输入记事内容".equals(content)) {
			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT)
					.show();
		} else {
			manage.addNote(sqlDB, name, content, time);
			Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT)
					.show();
			String[] t = txtTime.getText().toString().trim().split(" ");
			String[] t1 = t[0].split("-");
			String[] t2 = t[1].split(":");
			Calendar c2 = Calendar.getInstance();
			c2.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1,
					Integer.parseInt(t1[2]), Integer.parseInt(t2[0]),
					Integer.parseInt(t2[1]));
			calendar=Calendar.getInstance();
			if (calendar.getTimeInMillis() + 1000 * 10 <= c2.getTimeInMillis()) {
				String messageContent;
				if (content.length() > 20) {
					messageContent = content.substring(0, 18) + "…";
				} else {
					messageContent = content;
				}
				Intent intent = new Intent();
				intent.setClass(this, AlarmNote.class);
				intent.putExtra("messageTitle", name);
				intent.putExtra("messageContent", messageContent);
				pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				alarmManage = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarmManage.set(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), pendingIntent);
			}
			Intent intent2 = new Intent();
			intent2.setClass(this, MainActivity.class);
			startActivity(intent2);
		}
	}
}