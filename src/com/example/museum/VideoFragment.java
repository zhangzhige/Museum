package com.example.museum;

import java.util.List;

import com.example.museum.HttpManager.OnLoadFinishListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xunlei.common.httpclient.AsyncHttpProxy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class VideoFragment extends Fragment {

	private List<Cultural> mCulturalList;

	private Gallery fancyCoverFlow;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_inflate_example,
				container, false);
		initUI();
		return mRootView;
	}

	private void initUI() {
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(
				R.drawable.default_logo).build();
		fancyCoverFlow = (Gallery) mRootView.findViewById(R.id.fancyCoverFlow);
		fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
				Cultural item = mFancyCoverFlowSampleAdapter.getItem(position);
				String url = item.ProductPictures.get(1).PictureUrl;
				intent.putExtra("PictureUrl", url);
				startActivity(intent);
			}
		});

		new HttpManager().loadData(13,0,
				new OnLoadFinishListener<Cultural>() {

					@Override
					public void onLoad(List<Cultural> mList) {
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
		public Cultural getItem(int i) {
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
				reuseableView = inflater.inflate(R.layout.item_home, viewGroup,
						false);

				holder = new Holder();
				holder.imageView = (ImageView) reuseableView
						.findViewById(R.id.imageView1);
				reuseableView.setTag(holder);
			} else {
				holder = (Holder) reuseableView.getTag();
			}

			Cultural item = getItem(i);
			String url = item.ProductPictures.get(0).PictureUrl;
			ImageLoader.getInstance().displayImage(url, holder.imageView,
					mOptions);
			return reuseableView;
		}

		private class Holder {
			public ImageView imageView;
		}

	}
}
