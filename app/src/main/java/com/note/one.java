package com.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.note.util.SharePreferences;

public class one extends Activity {
    ActivityManager manager;
    SharePreferences preferences;
    //检测是否是第一次进入本应用
    Runnable runnable = new Runnable() {

        public void run() {
            // TODO Auto-generated method stub
            boolean isFirst = preferences.isFirstLogin(one.this);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);

        }
    };
    private ImageView img = null;
    private float X;
    private OnTouchListener ontouch = new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    X = event.getX();
                    break;

                case MotionEvent.ACTION_UP:

                    if (event.getX() < X) {

                        startActivity(new Intent(one.this, two.class));
                        finish();
                    }
                    break;
            }
            return true;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one);
        manager = ActivityManager.getInstance();
        manager.addActivity(one.this);

        preferences = new SharePreferences();
        img = (ImageView) findViewById(R.id.img);
        img.setOnTouchListener(ontouch);
    }
}
