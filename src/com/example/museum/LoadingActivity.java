package com.example.museum;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class LoadingActivity extends Activity {

	private XL_Log log = new XL_Log(LoadingActivity.class);

	private final static int GOTO_XLSHAREACTIVITY = 1;

	public static int lastUserVersion = 0;

	private ImageView imageView_first;
	

	private Handler loadingCompleteHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case GOTO_XLSHAREACTIVITY:
				intent.setClass(LoadingActivity.this, MainActivity.class);
				startActivity(intent);
				LoadingActivity.this.finish();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.color.gif_bkgcolor);
		setContentView(R.layout.activity_loading);
		
		imageView_first = (ImageView) findViewById(R.id.imageView_first);
		loadingCompleteHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				ObjectAnimator mFirst = ObjectAnimator.ofFloat(imageView_first, "alpha", 1f,0f);
				mFirst.setDuration(1500);
				mFirst.start();
				
				mFirst.addListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animator arg0) {
						loadingCompleteHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								loadingCompleteHandler.sendEmptyMessage(GOTO_XLSHAREACTIVITY);
							}
						}, 200);
					}
					
					@Override
					public void onAnimationCancel(Animator arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}, 400);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			LoadingActivity.this.finish();
			android.os.Process.killProcess(android.os.Process.myPid());
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
