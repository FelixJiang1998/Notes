package com.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class two extends Activity {
    ActivityManager manager;
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

                        overridePendingTransition(R.anim.right_out, R.anim.right_out);
                        Intent intent = new Intent(two.this, three.class);
                        startActivity(intent);
                        finish();
                    } else if (event.getX() > X) {

                        overridePendingTransition(R.anim.left_out, R.anim.left_out);
                        Intent intent = new Intent(two.this, one.class);
                        startActivity(intent);
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
        setContentView(R.layout.two);

        manager = ActivityManager.getInstance();
        manager.addActivity(two.this);
        img = (ImageView) findViewById(R.id.kbg2);
        img.setOnTouchListener(ontouch);
    }

}
