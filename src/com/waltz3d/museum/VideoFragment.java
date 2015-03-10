package com.waltz3d.museum;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixplicity.multiviewpager.MultiViewPager;
import com.pixplicity.multiviewpager.SimpleAdapter;
import com.pixplicity.multiviewpager.TabletTransformer;
import com.waltz3d.museum.HttpManager.OnLoadFinishListener;

public class VideoFragment extends BaseFragment {

	private List<HistoryVideo> mCulturalList;

	private MultiViewPager multiViewPager;

	private SimpleAdapter mSimpleAdapter;

	private View mRootView;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mCulturalList != null && mCulturalList.size() > 0) {
				mSimpleAdapter = new SimpleAdapter(getActivity(), mCulturalList);
				multiViewPager.setAdapter(mSimpleAdapter);
			}
		};
	};

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_inflate_example,container, false);
		initUI();
		return mRootView;
	}

	private void initUI() {
		multiViewPager = (MultiViewPager) mRootView.findViewById(R.id.pager);
		multiViewPager.setPageTransformer(true, new TabletTransformer());

		new HttpManager().loadHistoryVideoData(R.raw.history_video,
				new OnLoadFinishListener<HistoryVideo>() {

					@Override
					public void onLoad(List<HistoryVideo> mList) {
						if (mList != null && mList.size() > 0) {
							mCulturalList = mList;
						}
						mHandler.obtainMessage().sendToTarget();
					}
				});
	}
}
