package com.example.museum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.runmit.sweedee.util.gif.GifImageView;
import com.runmit.sweedee.util.gif.GifImageView.OnAnimationEndListener;
import com.runmit.sweedee.util.gif.GifMovieView;

public class LoadingActivity extends Activity {

	private XL_Log log = new XL_Log(LoadingActivity.class);

	private final static int GOTO_XLSHAREACTIVITY = 1;

	public final static String CURRENT_VERSION = "current_version";

	public static int lastUserVersion = 0;

	public static long appStartTime;

	// 安装启动模式
	private enum LaunchType {
		FIRST_INSTALL, // 首次安装
		UPDATE_INSTALL, // 覆盖安装启动
		NORMAL_LANUCH, // 普通启动
		UNKNOWN, // 覆盖安装但是之前版本并没有此字段
	}

	private Bitmap loadingBitmap = null;

	@SuppressLint("HandlerLeak")
	private Handler loadingCompleteHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			log.debug("handleMessage=" + msg.what);
			switch (msg.what) {
			case GOTO_XLSHAREACTIVITY:
				intent.setClass(LoadingActivity.this, MainActivity.class);
				Log.d("Loading", "activity start 2");
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
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		loadingCompleteHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setAnimView();
				appStartTime = System.currentTimeMillis();
			}
		}, 300);

	}

	private void setAnimView() {
		ViewGroup.LayoutParams lpParam = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		GifMovieView movieView = new GifMovieView(this);
		int duration = movieView.setMovieResource(R.drawable.start_animation);
		log.debug("setAnimView duration  = " + duration);
		if (duration > 0) {
			movieView.setOnEndListener(new GifMovieView.EndListener() {
				@Override
				public void onEnd() {
					log.debug("GifMovieView anim end ");
					loadingCompleteHandler.sendEmptyMessage(GOTO_XLSHAREACTIVITY);
				}
			});
			setContentView(movieView, lpParam);
		} else {// 手机系统不支持，用原有的方式
			GifImageView gifImageView = new GifImageView(this);
			gifImageView.setOnAnimationEndListener(new OnAnimationEndListener() {
				@Override
				public void onEnd() {
					log.debug("GifImageView anim end ");
					loadingCompleteHandler.sendEmptyMessage(GOTO_XLSHAREACTIVITY);
				}
			});
			gifImageView.setGifImage(R.drawable.start_animation);
			gifImageView.startAnimation();
			setContentView(gifImageView, lpParam);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (loadingBitmap != null && !loadingBitmap.isRecycled()) {
			loadingBitmap.recycle();
			loadingBitmap = null;
		}
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
