package com.waltz3d.museum.detail;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.waltz3d.museum.DownloadInfo;
import com.waltz3d.museum.XL_Log;

public class DetailGifView extends ImageView {

	XL_Log log = new XL_Log(DetailGifView.class);

	private Bitmap currentImage = null;

	private boolean isRun = true;

	private DrawThread drawThread = null;

	private SparseArray<Bitmap> mImageMap = new SparseArray<Bitmap>();

	private int mCurrentIndex = 0;

	private List<DownloadInfo> mList;

	private ImageView imageView_src;
	
	public void setImageView_src(ImageView imageView_src) {
		this.imageView_src = imageView_src;
	}

	private Handler redrawHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (currentImage != null) {
				setImageBitmap(currentImage);
				invalidate();
			}
		}
	};

	public DetailGifView(Context context) {
		this(context, null);

	}

	public DetailGifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DetailGifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setResList(List<DownloadInfo> mList) {
		this.mList = mList;
	}

	private Bitmap next() {
		if (mList != null && mList.size() > 0) {
			mCurrentIndex += 1;
			int index = mCurrentIndex % mList.size();
			Bitmap mBitmap = mImageMap.get(index);
			if (mBitmap == null) {
				mBitmap = BitmapFactory.decodeFile(mList.get(index).imgPath);
				mImageMap.put(index, mBitmap);
			}
			return mBitmap;
		}
		return null;
	}
	
	private Bitmap previous() {
		if (mList != null && mList.size() > 0) {
			mCurrentIndex -= 1;
			if(mCurrentIndex < 0){
				mCurrentIndex += mList.size();
			}
			int index = mCurrentIndex % mList.size();
			Bitmap mBitmap = mImageMap.get(index);
			if (mBitmap == null) {
				mBitmap = BitmapFactory.decodeFile(mList.get(index).imgPath);
				mImageMap.put(index, mBitmap);
			}
			
			return mBitmap;
		}
		return null;
	}

	public void start() {
		drawThread = new DrawThread();
		drawThread.start();
	}

	public void onStop() {
		isRun = false;
	}

	public void onResume() {
		if (!isRun) {
			isRun = true;
			drawThread = new DrawThread();
			drawThread.start();
		}
	}

	private float mLastX = 0;
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onStop();
			mLastX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(event.getX() - mLastX) > 12.0f){
				if(event.getX() > mLastX){//向右
					currentImage = next();
				}else{//向左
					currentImage = previous();
				}
				Message msg = redrawHandler.obtainMessage();
				redrawHandler.sendMessage(msg);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			onResume();
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 动画线程
	 *
	 */
	private class DrawThread extends Thread {
		public void run() {
			while (isRun) {
				currentImage = next();
				Message msg = redrawHandler.obtainMessage();
				redrawHandler.sendMessage(msg);
				try {
					Thread.sleep(90);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
