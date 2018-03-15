package com.psy.my;

import java.util.Timer;
import java.util.TimerTask;

import camerakit.TakePhoto;
import cn.xdu.poscam.R;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;

import com.psy.db.DataAccess;


public class SplashScreenActivity extends  Activity {

	public SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager mam = MyActivityManager.getInstance();
		mam.pushOneActivity(SplashScreenActivity.this);
		this.setContentView(R.layout.splash_screen);


		final Intent intent = new Intent();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				sp = getSharedPreferences("flagSP", 0);
				SharedPreferences.Editor editor1 = sp.edit();

				if (sp.getInt("flag", 0) == 0) {
					DataAccess access = new DataAccess();
					access.initDatabase(SplashScreenActivity.this);
					editor1.putInt("flag", 1);
					editor1.commit();

					intent.setClass(SplashScreenActivity.this, GuideActivity.class);
					finish();
					SplashScreenActivity.this.startActivity(intent);

				}else {

					//if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
						// your code using Camera API here - is between 1-20
						intent.setClass(SplashScreenActivity.this, TakePhoto.class);
						finish();
						SplashScreenActivity.this.startActivity(intent);
//					} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//						// your code using Camera2 API here - is api 21 or higher
//						Common.display(SplashScreenActivity.this,"暂不支持安卓5.0及以上版本...");
//					}
				}
			}
		};
		timer.schedule(task, 1400);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}

