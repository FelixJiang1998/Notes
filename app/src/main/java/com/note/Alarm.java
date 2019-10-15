package com.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

public class Alarm extends Activity {
    private MediaPlayer mMediaPlayer;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        try {
            mMediaPlayer = MediaPlayer.create(Alarm.this, ActivityManager.getUri());
            mMediaPlayer.setVolume(300, 350);
            mMediaPlayer.setLooping(true);
        } catch (Exception e) {
            Toast.makeText(Alarm.this, "音乐文件播放异常", Toast.LENGTH_SHORT);
        }
        mMediaPlayer.start();
        if (mMediaPlayer.isPlaying()) {
            System.out.println("is palying");
        } else {
            System.out.println("not playing");
        }
        Intent intent = getIntent();
        String messageTitle = intent.getStringExtra("messageTitle");
        String messageContent = intent.getStringExtra("messageContent");
        AlertDialog.Builder adb = new Builder(Alarm.this);
        adb.setTitle(messageTitle);
        adb.setMessage(messageContent);
        adb.setPositiveButton("确定", new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                finish();
            }
        });
        adb.show();

    }
}
