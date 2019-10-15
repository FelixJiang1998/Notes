package com.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.note.dao.SqliteDBConnect;

import java.io.File;

public class AddNewVoice extends Activity implements android.view.View.OnClickListener {
    private static final String TAG = "XU";
    MediaRecorder recorder;
    File sdcardPath = null;
    File recordPath = null;
    String prefix = "RecordFile_"; //要保存的录音文件的前缀
    ActivityManager manager;
    File file;
    String returnName;
    Button btnStartRecord, btnStopRecord, btnSaveReturn, btnNoSaveReturn, btnExit;
    int category = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_voice);


        manager = ActivityManager.getInstance();
        manager.addActivity(this);

        recorder = new MediaRecorder();
        sdcardPath = Environment.getExternalStorageDirectory();

        try {
            recordPath = File.createTempFile(prefix, ".amr", sdcardPath); //三个参数分别为前缀、后缀、目录
            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//设置音频来源，
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);// 设置输出格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //编码格式采用默认
            recorder.setOutputFile(recordPath.getAbsolutePath()); //设置输出路径
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnStartRecord = (Button) this.findViewById(R.id.btnStart);
        btnStopRecord = (Button) this.findViewById(R.id.btnStop);
        btnSaveReturn = (Button) this.findViewById(R.id.btnSaveReturn);
        btnNoSaveReturn = (Button) this.findViewById(R.id.btnNoSaveReturn);
        btnExit = (Button) this.findViewById(R.id.btnExit);


        btnStartRecord.setOnClickListener(this);
        btnStopRecord.setOnClickListener(this);
        btnSaveReturn.setOnClickListener(this);
        btnNoSaveReturn.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }

    public String showMyDialog() {
        final EditText input = new EditText(AddNewVoice.this);
        AlertDialog.Builder adb = new Builder(AddNewVoice.this);
        adb.setTitle("提示");
        adb.setView(input);
        adb.setMessage("请输入备忘笔记的名字");
        adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                returnName = input.getText().toString().trim();
            }
        });
        adb.show();
        return returnName;
    }

    public void onClick(View v) {

        //开始录音
        if (v.getId() == R.id.btnStart) {
            try {
                recorder.prepare();
                recorder.start();
                Toast.makeText(AddNewVoice.this, "正在录音...", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            btnStartRecord.setEnabled(false);
            btnSaveReturn.setEnabled(false);
            btnNoSaveReturn.setEnabled(false);
            btnExit.setEnabled(false);
            btnStopRecord.setEnabled(true);
        }
        //结束录音
        else if (v.getId() == R.id.btnStop) {
            recorder.stop();
            recorder.release();
            recorder = null;

            returnName = showMyDialog();

            btnStartRecord.setEnabled(true);
            btnSaveReturn.setEnabled(true);
            btnNoSaveReturn.setEnabled(true);
            btnExit.setEnabled(true);
            btnStopRecord.setEnabled(false);
        }
        //保存返回
        else if (v.getId() == R.id.btnSaveReturn) {
            if (!(returnName.equals(""))) {
                addNote();
            } else {
                Toast.makeText(AddNewVoice.this, "名字为空，保存失败", Toast.LENGTH_SHORT).show();

                returnName = showMyDialog();
                if (!(returnName.equals(""))) {
                    addNote();
                }
            }
//				btnStopRecord.setEnabled(true);
        }

        //不保存返回
        else if (v.getId() == R.id.btnNoSaveReturn) {
            Intent intent = new Intent(AddNewVoice.this, MainActivity.class);
            startActivity(intent);
            btnStopRecord.setEnabled(true);
        }

        //退出
        else if (v.getId() == R.id.btnExit) {
            manager.exitAllProgress();
            btnStopRecord.setEnabled(true);
        }
    }

    public void addNote() {
        String content = recordPath.getAbsolutePath();
        String name = returnName;
        String time = manager.returnTime();
        SqliteDBConnect connect = new SqliteDBConnect(AddNewVoice.this);
        SQLiteDatabase database = connect.getReadableDatabase();
        manager.addNote(database, name, content, time, category);
        Intent intent = new Intent(AddNewVoice.this, ShowVoiceList.class);
        startActivity(intent);
        Toast.makeText(AddNewVoice.this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
	

