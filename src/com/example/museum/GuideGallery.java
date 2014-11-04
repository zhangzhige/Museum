package com.example.museum;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GuideGallery extends RelativeLayout {

	public static final float ASPECT_RATIO = 344.0f / 720;

	private static final int DEFINE_SCROLL_TIME = 4 * 1000;

	private XL_Log log = new XL_Log(GuideGallery.class);

	private LayoutInflater mInflater;

	private ViewPager mPager;

	private TextView title_textview;

	private ImageTimerTask mImageTimerTask;

	private Timer mTimer;

	private Handler mHandler = new Handler();

	private PageIndicator mIndicator;

	private ImageLoader mImageLoader;

	private DisplayImageOptions mOptions;

	private ImageAdapter mImageAdapter;

	private Runnable myRunnable = new Runnable() {
		@Override
		public void run() {
			onResume();
		}
	};

	public GuideGallery(Context context) {
		super(context);
		init();
	}

	public GuideGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		float density = getResources().getDisplayMetrics().density;
		mInflater = LayoutInflater.from(getContext());
		mInflater.inflate(R.layout.guide_gallery, this);
		mPager = (ViewPager) findViewById(R.id.image_wall_gallery);
		mPager.setPageMargin((int) (25 * density));

		title_textview = (TextView) findViewById(R.id.title_textview);

		mImageLoader = ImageLoader.getInstance();
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_banner_image).build();
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

	public void setDataSource(ArrayList<CmsItemable> mArrayList) {
		mImageAdapter = new ImageAdapter(mArrayList);
		mPager.setAdapter(mImageAdapter);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		title_textview.setText(mImageAdapter.getItem(0).getTitle());
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				title_textview.setText(mImageAdapter.getItem(position % mImageAdapter.getRealCount()).getTitle());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub

			}
		});
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
					int gallerypisition = mPager.getCurrentItem() + 1;
					mPager.setCurrentItem(gallerypisition, true);
				}
			});
		}
	}

	private class ImageAdapter extends RecyclingPagerAdapter {

		private ArrayList<CmsItemable> items;

		public ImageAdapter(ArrayList<CmsItemable> mArrayList) {
			this.items = mArrayList;

		}

		public int getCount() {
			return Integer.MAX_VALUE;
		}

		public CmsItemable getItem(int position) {
			return items.get(position);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder n = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_resource_suggest_head, null);
				n = new ViewHolder();
				n.gallery_image = (ImageView) convertView.findViewById(R.id.gallery_image);
				convertView.setTag(n);
			} else {
				n = (ViewHolder) convertView.getTag();
			}
			final CmsItemable mItem = getItem(position);
			if (mItem != null) {
				String imgUrl = mItem.getPosterUrl();
				mImageLoader.displayImage(imgUrl, n.gallery_image, mOptions);
			}
			n.gallery_image.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

				}
			});
			return convertView;
		}

		class ViewHolder {
			public ImageView gallery_image;
		}

		@Override
		public int getRealCount() {
			if (items != null) {
				return items.size();
			}
			return 0;
		}
	}
}
