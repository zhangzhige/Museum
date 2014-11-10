/**
 * XlMainView.java 
 * com.xunlei.share.XlMainView
 * @author: Administrator
 * @date: 2013-4-18 下午5:24:31
 */
package com.example.museum;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author Administrator 实现的主要功能。
 * 
 *         修改记录：修改者，修改日期，修改内容
 */
public class MainView extends LinearLayout implements OnClickListener {

	final XL_Log log = new XL_Log(MainView.class);

	public final static String TAB_HOMEPAGE = "tab_1";
	public final static String TAB_VIDEO = "tab_2";
	public final static String TAB_CULTURAL = "tab_3";
	public final static String TAB_SIGNATURE = "tab_4";
	public final static String TAB_MESEUM_LOCATION = "tab_5";

	final LayoutInflater inflater;
	private FragmentActivity mFragmentActivity;

	private Fragment lastFragment = null;

	private HomeFragment mHomeFragment;
	private VideoFragment mVideoFragment;
	private CulturalFragment mCulturalFragment;
	private SignatureWallFragment mSignatureWallFragment;
	public LocationFragment mLocationFragment;

	private ImageView[] imageViewArray = new ImageView[5];
	private ViewGroup[] linearLayoutArray = new ViewGroup[5];

	private int[] menu_image_array_common = { R.drawable.icon_home_normal,  R.drawable.icon_video_normal,R.drawable.icon_stat_normal,R.drawable.icon_pen_normal,R.drawable.icon_location_normal };

	private int[] menu_image_array_click = { R.drawable.icon_home_done,R.drawable.icon_video_done,R.drawable.icon_stat_done,  R.drawable.icon_pen_done,R.drawable.icon_location_done };
	
	private GifView gifView;
	/**
	 * @param context
	 */
	public MainView(FragmentActivity context, Bundle savedInstanceState) {
		super(context);
		this.mFragmentActivity = context;
		inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.activity_main, this);
		onFinishInflate(savedInstanceState);
	}

	private void onFinishInflate(Bundle savedInstanceState) {
		
		gifView = (GifView) findViewById(R.id.gifView);
		gifView.start();
		
		imageViewArray[0] = (ImageView) findViewById(R.id.imageview1);
		imageViewArray[1] = (ImageView) findViewById(R.id.imageview2);
		imageViewArray[2] = (ImageView) findViewById(R.id.imageview3);
		imageViewArray[3] = (ImageView) findViewById(R.id.imageview4);
		imageViewArray[4] = (ImageView) findViewById(R.id.imageview5);


		linearLayoutArray[0] = (LinearLayout) findViewById(R.id.home_layout);
		linearLayoutArray[1] = (LinearLayout) findViewById(R.id.video_layout);
		linearLayoutArray[2] = (LinearLayout) findViewById(R.id.cultural_layout);
		linearLayoutArray[3] = (LinearLayout) findViewById(R.id.signature_layout);
		linearLayoutArray[4] = (LinearLayout) findViewById(R.id.meseum_location_layout);
		for (int i = 0, size = linearLayoutArray.length; i < size; i++) {
			linearLayoutArray[i].setOnClickListener(this);
		}
		switchTabs(R.id.home_layout);
		
	}

	private void changeFragment(Fragment f, String name) {
		changeFragment(f, name, null);
	}

	/**
	 * 
	 * 方法的一句话概述
	 * <p>
	 * 方法详述（简单方法可不必详述）
	 * </p>
	 * 
	 * @param f
	 * @param name
	 * @param destroyFrament
	 *            :要销毁的fragment，传空表示正常操作
	 */
	private void changeFragment(Fragment f, String name, Fragment destroyFrament) {
		FragmentTransaction mFragmentTransaction = mFragmentActivity.getSupportFragmentManager().beginTransaction();
		log.debug("changefragment lastFragment=" + lastFragment);
		if (lastFragment != null && lastFragment != f) {
			mFragmentTransaction.detach(lastFragment);
		}
		if (destroyFrament != null) {
			mFragmentTransaction.remove(destroyFrament);
		}
		log.debug("changefragment isAdded=" + f.isAdded() + ",f=" + f.getClass().getName());
		if (!f.isAdded()) {
			mFragmentTransaction.add(R.id.center_layout, f, name);
		}
		if (f.isDetached()) {
			mFragmentTransaction.attach(f);
		}
		lastFragment = f;
		mFragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		switchTabs(v.getId());
	}

	public void switchTabs(int tab_id) {
		switch (tab_id) {
		case R.id.cultural_layout:
			if (mCulturalFragment == null) {
				mCulturalFragment = new CulturalFragment();
			}
			changeFragment(mCulturalFragment, TAB_CULTURAL);
			changeBottomUI(2);
			break;
		case R.id.video_layout:
			if (mVideoFragment == null) {
				mVideoFragment = new VideoFragment();
			}
			changeFragment(mVideoFragment, TAB_VIDEO);
			changeBottomUI(1);
			break;
		case R.id.signature_layout:
			if (mSignatureWallFragment == null) {
				mSignatureWallFragment = new SignatureWallFragment();
			}
			changeFragment(mSignatureWallFragment, TAB_SIGNATURE);
			changeBottomUI(3);
			break;
		case R.id.meseum_location_layout:
			if (mLocationFragment == null) {
				mLocationFragment = new LocationFragment();
			}
			changeFragment(mLocationFragment, TAB_MESEUM_LOCATION);
			changeBottomUI(4);
			break;
		case R.id.home_layout:
		default:
			if (mHomeFragment == null) {
				mHomeFragment = new HomeFragment();
			}
			changeFragment(mHomeFragment, TAB_HOMEPAGE);
			changeBottomUI(0);
			break;
		}

	}

	private void changeBottomUI(int tab_id) {
		gifView.setVisibility(tab_id < 2 ? View.VISIBLE:View.INVISIBLE);
		for (int i = 0, size = imageViewArray.length; i < size; i++) {
			if (i == tab_id) {
				imageViewArray[i].setImageResource(menu_image_array_click[i]);
			} else {
				imageViewArray[i].setImageResource(menu_image_array_common[i]);
			}
		}
	}

	/**
	 * 方法的一句话概述
	 * <p>
	 * 方法详述（简单方法可不必详述）
	 * </p>
	 */
	public void onDestroy() {
		if (mHomeFragment != null) {
			mHomeFragment.onDestroy();
		}
		if (mCulturalFragment != null) {
			mCulturalFragment.onDestroy();
		}
		if (mVideoFragment != null) {
			mVideoFragment.onDestroy();
		}
		if (mSignatureWallFragment != null) {
			mSignatureWallFragment.onDestroy();
		}
	}
}
