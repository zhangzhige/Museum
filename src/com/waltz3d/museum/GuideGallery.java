package com.waltz3d.museum;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GuideGallery extends FrameLayout {

	public static final float ASPECT_RATIO = 344.0f / 720;

	private static final int DEFINE_SCROLL_TIME = 4 * 1000;

	private XL_Log log = new XL_Log(GuideGallery.class);

	private ImageTimerTask mImageTimerTask;

	private Timer mTimer;

	private Handler mHandler = new Handler();
	
	private int currentIndex = 0;

	private Runnable myRunnable = new Runnable() {
		@Override
		public void run() {
			onResume();
		}
	};

	public GuideGallery(Context context) {
		super(context);
		setBackgroundResource(R.drawable.collection_head_bg);
	}

	public GuideGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackgroundResource(R.drawable.collection_head_bg);
	}

	private void resumeTimeTaskDelay() {
		mHandler.removeCallbacks(myRunnable);
		mHandler.postDelayed(myRunnable, DEFINE_SCROLL_TIME);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		log.debug("onInterceptTouchEvent ev=" + ev.getAction());
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onStop();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			resumeTimeTaskDelay();
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	public void setAdapter(RecyclingPagerAdapter mRecyclingPagerAdapter) {
		FrameLayout.LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		for(int i = 0,size = mRecyclingPagerAdapter.getRealCount();i<size;i++){
			View v = mRecyclingPagerAdapter.getView(i, null, this);
			v.setAlpha(i == currentIndex ? 1:0);
			addView(v, mParams);
		}
	}


	public void onResume() {
		onStop();
		mTimer = new Timer();
		mImageTimerTask = new ImageTimerTask();
		mTimer.schedule(mImageTimerTask, DEFINE_SCROLL_TIME, DEFINE_SCROLL_TIME);
	}

	public void onStop() {
		mHandler.removeCallbacks(myRunnable);
		if (mImageTimerTask != null) {
			mImageTimerTask.cancel();
			mImageTimerTask = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private class ImageTimerTask extends TimerTask {
		public void run() {
			mHandler.post(new Runnable() {
				public void run() {
					View currentV = getChildAt(currentIndex % getChildCount());
					ObjectAnimator mAnimator = ObjectAnimator.ofFloat(currentV, "alpha", 1,0);
					mAnimator.setDuration(800);
					mAnimator.start();
					
					currentIndex +=1;
					View nextV = getChildAt(currentIndex % getChildCount());
					ObjectAnimator mNextAnimator = ObjectAnimator.ofFloat(nextV, "alpha", 0,1);
					mNextAnimator.setDuration(800);
					mNextAnimator.start();
				}
			});
		}
	}
}
