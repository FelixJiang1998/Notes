package com.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainUIActivity extends Activity implements OnClickListener {
    ActivityManager manager;
    /**
     * Called when the activity is first created.
     */
    private ImageButton btntxtNote = null;
    private ImageButton btnVoiceNote = null;
    private ImageButton btnVideoNote = null;
    private ImageButton btnGengduo = null;
    private TextView tx1 = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        manager = ActivityManager.getInstance();
        manager.addActivity(this);

        tx1 = (TextView) findViewById(R.id.tx1);

        //文本语音视频和所有备忘录的按钮
        btntxtNote = (ImageButton) findViewById(R.id.txtNote);
        btnVoiceNote = (ImageButton) findViewById(R.id.voiceNote);
        btnVideoNote = (ImageButton) findViewById(R.id.reminder);
        btnGengduo = (ImageButton) findViewById(R.id.gengduo);

        //设置文本语音视频和所有备忘录的按钮点击事件
        btntxtNote.setOnClickListener(this);
        btnVoiceNote.setOnClickListener(this);
        btnVideoNote.setOnClickListener(this);
        btnGengduo.setOnClickListener(this);
    }

    public void onClick(View v) {

        //文本备忘录
        if (v.getId() == R.id.txtNote) {
            Intent intent = new Intent(MainUIActivity.this, ShowTextList.class);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            startActivity(intent);
        }

        //语音备忘录
        else if (v.getId() == R.id.voiceNote) {
            Intent intent = new Intent(MainUIActivity.this, ShowVoiceList.class);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            startActivity(intent);
        }

        //视频备忘录
        else if (v.getId() == R.id.reminder) {
//				Toast.makeText(MainUIActivity.this, "由于时间有限该模块功能暂未实现", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainUIActivity.this, AddNewVideo.class);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            startActivity(intent);
        }

        //所有备忘录
        else if (v.getId() == R.id.gengduo) {
            Intent intent = new Intent(MainUIActivity.this, MainActivity.class);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            startActivity(intent);
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
                Intent intent = new Intent();
                intent.setClass(MainUIActivity.this, SetAlarm.class);
                startActivity(intent);
                break;
            case 2:
                AlertDialog.Builder adb2 = new Builder(MainUIActivity.this);
                adb2.setTitle("消息");
                adb2.setMessage("确定要退出吗？");
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


    //手机的返回(后退)按钮的事件处理
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder adb = new Builder(MainUIActivity.this);
            adb.setTitle("消息");
            adb.setMessage("确认退出？");
            adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    manager.exitAllProgress();

                }
            });
            adb.setNegativeButton("取消", null);
            adb.show();
        }
        return super.onKeyDown(keyCode, event);
    }


}
