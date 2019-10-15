package com.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class three extends Activity {
    ActivityManager manager;
    private ImageView img = null;
    private float X;
    private Button btn = null;
    private OnTouchListener ontouch = new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    X = event.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    if (event.getX() > X) {
                        Intent intent = new Intent(three.this, two.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
            return true;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.three);

        manager = ActivityManager.getInstance();
        manager.addActivity(three.this);

        img = (ImageView) findViewById(R.id.kbg3);
        img.setOnTouchListener(ontouch);
        btn = (Button) findViewById(R.id.btn1);
        btn.setOnClickListener(new tomain());
    }

    class tomain implements OnClickListener {

        public void onClick(View v) {
            Intent intent = new Intent(three.this, MainUIActivity.class);
            overridePendingTransition(R.anim.left_in, R.anim.left_in);
            startActivity(intent);
            finish();
        }
    }
}
