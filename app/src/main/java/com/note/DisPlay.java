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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.note.dao.SqliteDBConnect;

import java.util.Calendar;

public class DisPlay extends Activity {
    private static final String TAG = "XU";
    String getName, getContent, getTime;
    int getCategory;
    MediaPlayer player;
    String category = null;
    private Button btnCommit, btnCancel;
    private EditText txtName, txtContent;
    private TextView txtTime;
    private String noteId;
    private SQLiteDatabase sdb;
    private ActivityManager am;
    private Calendar c;
    private int year, month, day, hours, minute, second;
    private PendingIntent pi;
    private AlarmManager alm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        am = ActivityManager.getInstance();
        am.addActivity(this);

        player = new MediaPlayer();//用来播放录音的音频文件

        Intent intent = getIntent();
        noteId = intent.getStringExtra("noteId");
        category = intent.getStringExtra("category");

        btnCommit = (Button) findViewById(R.id.btnCommit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtName = (EditText) findViewById(R.id.noteName);
        txtContent = (EditText) findViewById(R.id.noteMain);
        txtTime = (TextView) findViewById(R.id.noteTime);
        txtContent.setEnabled(true);

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hours = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        second = c.get(Calendar.SECOND);

        SqliteDBConnect sd = new SqliteDBConnect(DisPlay.this);
        sdb = sd.getReadableDatabase();
        Cursor c = sdb.query("note", new String[]{"noteId", "noteName",
                        "noteContent", "noteTime", "category"}, "noteId=?",
                new String[]{noteId}, null, null, null);

        while (c.moveToNext()) {
            getName = c.getString(c.getColumnIndex("noteName"));
            getContent = c.getString(c.getColumnIndex("noteContent"));
            getTime = c.getString(c.getColumnIndex("noteTime"));
            getCategory = c.getInt(c.getColumnIndex("category"));

            txtName.setText(getName);
            txtContent.setText(getContent);
            txtTime.setText(getTime);
        }
        c.close();

        try {
            //如果从数据库读取的类别号是2则调用播放函数处理
            if (getCategory == 2) {
                txtContent.setEnabled(false);
                play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        txtTime.setTextColor(Color.RED);
        btnCommit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int n = testTime();
                if (n == 0) {
                    return;
                }
                AlertDialog.Builder adb = new Builder(DisPlay.this);
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
                                Toast.makeText(DisPlay.this, "Don't Save",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                adb.show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.exit(0);
            }
        });
        txtTime.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(DisPlay.this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int y,
                                                  int monthOfYear, int dayOfMonth) {
                                String[] time = {"",
                                        hours + ":" + minute + ":" + second};
                                try {
                                    String[] time2 = txtTime.getText().toString().trim()
                                            .split(" ");
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
                TimePickerDialog tpd = new TimePickerDialog(DisPlay.this,
                        new OnTimeSetListener() {

                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                String[] time = {year + "-" + month + "-" + day, ""};
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
    }

    //这个方法用来判断并播放录音文件
    public void play() {
        player.reset();
        try {
            player.setDataSource(getContent);
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int testTime() {
        int flag = 0;
        try {
            String[] t = txtTime.getText().toString().trim().split(" ");
            String[] t1 = t[0].split("-");
            String[] t2 = t[1].split(":");
            if (t1.length != 3)
                flag = 1;
            if (t2.length != 2 && t2.length != 3)
                flag = 1;
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
            if (t2.length == 3) {
                int s = Integer.parseInt(t2[2]);
                if (s < 0 || s > 60)
                    flag = 1;
            }
            if (flag == 1) {
                Toast.makeText(DisPlay.this, "时间格式不正确，请单击或长按时间框设置",
                        Toast.LENGTH_SHORT).show();
                return 0;
            }

            Calendar c2 = Calendar.getInstance();
            c2.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1,
                    Integer.parseInt(t1[2]), Integer.parseInt(t2[0]),
                    Integer.parseInt(t2[1]));
        } catch (Exception e) {
            Toast.makeText(DisPlay.this, "日期时间不正确，请单击或长按时间框设置",
                    Toast.LENGTH_SHORT).show();
            return 0;
        }
        return 1;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "关于");
        menu.add(0, 2, 2, "设置闹铃声");
        menu.add(0, 3, 3, "退出");
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AlertDialog.Builder adb = new Builder(DisPlay.this);
                adb.setTitle("关于我们");
                adb.setMessage("Android团队制作");
                adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DisPlay.this, "谢谢使用", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                adb.show();
                break;
            case 2:
                Intent intent = new Intent();
                intent.setClass(DisPlay.this, SetAlarm.class);
                startActivity(intent);
                break;
            case 3:
                AlertDialog.Builder adb2 = new Builder(DisPlay.this);
                adb2.setTitle("消息");
                adb2.setMessage("真的要退出吗？");
                adb2.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        am.exitAllProgress();
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
        String getName2 = txtName.getText().toString().trim();
        String getContent2 = txtContent.getText().toString().trim();
        String getTime2 = txtTime.getText().toString().trim();


        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (getName.equals(getName2) && getContent.equals(getContent2) && getTime.equals(getTime2)) {
                if (category.equals("voice")) {
                    Intent intent2 = new Intent();
                    intent2.setClass(DisPlay.this, ShowVoiceList.class);
                    startActivity(intent2);
                }
                if (category.equals("text")) {
                    Intent intent2 = new Intent();
                    intent2.setClass(DisPlay.this, ShowTextList.class);
                    startActivity(intent2);
                }
            } else {

                AlertDialog.Builder adb = new Builder(DisPlay.this);
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
                        if (category.equals("voice")) {
                            Intent intent2 = new Intent();
                            intent2.setClass(DisPlay.this, ShowVoiceList.class);
                            startActivity(intent2);
                        }
                        if (category.equals("text")) {
                            Intent intent2 = new Intent();
                            intent2.setClass(DisPlay.this, ShowTextList.class);
                            startActivity(intent2);
                        }
                    }
                });
                adb.show();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void baocunNote() {
        String name = txtName.getText().toString().trim();
        String content = txtContent.getText().toString().trim();
        String time = txtTime.getText().toString().trim();
        if ("".equals(name) || "".equals(content)) {
            Toast.makeText(this, "名称和内容都不能为空", Toast.LENGTH_SHORT)
                    .show();
        } else if ("请输入记事名称".equals(name) || "请输入记事内容".equals(content)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
        } else {
            am.saveNote(sdb, name, content, noteId, time, getCategory);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            String[] t = txtTime.getText().toString().trim().split(" ");
            System.out.println(t[0] + ":" + t[1]);
            String[] t1 = t[0].split("-");
            String[] t2 = t[1].split(":");
            Calendar c2 = Calendar.getInstance();
            c2.set(Integer.parseInt(t1[0]), Integer.parseInt(t1[1]) - 1,
                    Integer.parseInt(t1[2]), Integer.parseInt(t2[0]),
                    Integer.parseInt(t2[1]));
            c = Calendar.getInstance();

            if (c.getTimeInMillis() + 1000 * 10 <= c2.getTimeInMillis()) {
                String messageContent;
                if (content.length() > 20) {
                    messageContent = content.substring(0, 18) + "…";
                } else {
                    messageContent = content;
                }
                Intent intent = new Intent();
                intent.setClass(this, AlarmNote.class);
                intent.putExtra("messageTitle", txtName.getText().toString());
                intent.putExtra("messageContent", messageContent);
                pi = PendingIntent.getBroadcast(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                alm = (AlarmManager) getSystemService(ALARM_SERVICE);
                alm.set(AlarmManager.RTC_WAKEUP, c2.getTimeInMillis(), pi);
            }

            if (category.equals("voice")) {
                Intent intent2 = new Intent();
                intent2.setClass(DisPlay.this, ShowVoiceList.class);
                startActivity(intent2);
            }
            if (category.equals("text")) {
                Intent intent2 = new Intent();
                intent2.setClass(DisPlay.this, ShowTextList.class);
                startActivity(intent2);
            }
        }
    }
}
