package com.note;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.note.dao.SqliteDBConnect;

import java.io.File;
import java.io.IOException;

public class AddNewVideo extends Activity {

    String filePath, fileName, prefix = "VideoFie_";
    File sdcardPath = null;
    File videoPath = null;
    ActivityManager manager;
    private File myRecAudioFile;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Button buttonStart;
    private Button buttonStop;
    private File dir;
    private boolean isStart = false;
    private MediaRecorder recorder;
    private String SD_CARD_TEMP_DIR = getSDPath() + File.separator + "myvideo"
            + File.separator;

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        } else {
            return null;
        }
        return sdDir.toString();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_video);

        manager = ActivityManager.getInstance();
        manager.addActivity(this);

        mSurfaceView = (SurfaceView) findViewById(R.id.videoView);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        buttonStart = (Button) findViewById(R.id.btnStart);
        buttonStop = (Button) findViewById(R.id.btnStop);
        buttonStop.setEnabled(false);
        dir = new File(SD_CARD_TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
        recorder = new MediaRecorder();

        sdcardPath = Environment.getExternalStorageDirectory();
        try {
            //三个参数分别为前缀、后缀、目录
            videoPath = File.createTempFile(prefix, ".amr", sdcardPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //开始录像
        buttonStart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                isStart = true;
                recorder();
                buttonStop.setEnabled(true);
            }
        });

        //停止录像
        buttonStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                addNote();//添加该条记录
            }
        });
    }

    //配置录制
    public void recorder() {
        try {

            myRecAudioFile = File.createTempFile("video", ".3gp", dir);// 创建临时文件
            recorder.setPreviewDisplay(mSurfaceHolder.getSurface());// 预览
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 录音源为麦克风
            recorder.setMaxDuration(1000000);// 最大期限
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 从照相机采集视频
            recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            recorder.setVideoSize(480, 320);
            recorder.setVideoFrameRate(3); // 每秒3帧
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT); // 设置视频编码方式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());// 保存路径
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNote() {
        String content = sdcardPath.getAbsolutePath();
        String name = "returnName";
        String time = manager.returnTime();
        SqliteDBConnect connect = new SqliteDBConnect(AddNewVideo.this);
        SQLiteDatabase database = connect.getReadableDatabase();
        manager.addNote(database, name, content, time, 3);
        Intent intent = new Intent(AddNewVideo.this, ShowVoiceList.class);
        startActivity(intent);
        Toast.makeText(AddNewVideo.this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }

}
