package com.psy.my;

import java.util.Timer;
import java.util.TimerTask;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;


public class GuideActivity extends Activity {
    private Button btnStart;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager mam = MyActivityManager.getInstance();
        mam.pushOneActivity(GuideActivity.this);
        this.setContentView(R.layout.guide);
        btnStart = (Button) findViewById(R.id.btnStart);

        final Intent intent = new Intent();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    // your code using Camera API here - is between 1-20
                intent.setClass(GuideActivity.this, TakePhoto.class);
                finish();
                GuideActivity.this.startActivity(intent);
                timer.cancel();

//                } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    // your code using Camera2 API here - is api 21 or higher
//                    Common.display(GuideActivity.this,"暂不支持安卓5.0及以上版本...");
//                }

            }
        });

        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    // your code using Camera API here - is between 1-20
                intent.setClass(GuideActivity.this, TakePhoto.class);
                finish();
                GuideActivity.this.startActivity(intent);
//                } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    // your code using Camera2 API here - is api 21 or higher
//                    Common.display(GuideActivity.this,"暂不支持安卓5.0及以上版本...");
//                }

            }
        };
        timer.schedule(task, 10000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

