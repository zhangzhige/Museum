package com.waltz3d.museum;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.waltz3d.museum.HttpManager.OnLoadFinishListener;

public class VideoFragment extends BaseFragment {

	private List<HistoryVideo> mCulturalList;

	private CoverFlow fancyCoverFlow;

	private DisplayImageOptions mOptions;

	private FancyCoverFlowSampleAdapter mFancyCoverFlowSampleAdapter;

	private View mRootView;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mCulturalList != null && mCulturalList.size() > 0) {
				mFancyCoverFlowSampleAdapter = new FancyCoverFlowSampleAdapter();
				fancyCoverFlow.setAdapter(mFancyCoverFlowSampleAdapter);
			}
		};
	};

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_inflate_example,
				container, false);
		initUI();
		return mRootView;
	}

	private void initUI() {
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(
				R.drawable.default_logo).build();
		fancyCoverFlow = (CoverFlow) mRootView.findViewById(R.id.fancyCoverFlow);
		fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
				HistoryVideo item = mFancyCoverFlowSampleAdapter.getItem(position);
				String url = item.url;
				intent.putExtra("url", url);
				startActivity(intent);
			}
		});

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

	public class FancyCoverFlowSampleAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public FancyCoverFlowSampleAdapter() {
			this.inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			return mCulturalList.size();
		}

		@Override
		public HistoryVideo getItem(int i) {
			return mCulturalList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View reuseableView, ViewGroup viewGroup) {
			Holder holder;
			if (reuseableView == null) {
				reuseableView = inflater.inflate(R.layout.item_video, viewGroup,
						false);

				holder = new Holder();
				holder.imageView = (ImageView) reuseableView.findViewById(R.id.imageView1);
				reuseableView.setTag(holder);
			} else {
				holder = (Holder) reuseableView.getTag();
			}

			HistoryVideo item = getItem(i);
			String url = item.photo;
			ImageLoader.getInstance().displayImage(url, holder.imageView,mOptions);
			return reuseableView;
		}

		private class Holder {
			public ImageView imageView;
		}

	}
}
