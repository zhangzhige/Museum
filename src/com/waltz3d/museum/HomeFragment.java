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
import com.waltz3d.common.httpclient.AsyncHttpProxy;
import com.waltz3d.museum.Cultural.ProductPicture;
import com.waltz3d.museum.HttpManager.OnLoadFinishListener;

public class HomeFragment extends BaseFragment {
	
	private View mRootView;
	
	private AsyncHttpProxy mHttpProxy = AsyncHttpProxy.getInstance();
	
	private List<Cultural> mCulturalList;
	
	private CoverFlow fancyCoverFlow;
	
	private DisplayImageOptions mOptions;
	
	private FancyCoverFlowSampleAdapter mFancyCoverFlowSampleAdapter;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(mCulturalList!=null && mCulturalList.size()>0){
				mFancyCoverFlowSampleAdapter = new FancyCoverFlowSampleAdapter();
				fancyCoverFlow.setAdapter(mFancyCoverFlowSampleAdapter);
			}
		};
	};
	
	public class FancyCoverFlowSampleAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		
		public FancyCoverFlowSampleAdapter(){
			this.inflater = LayoutInflater.from(MainApplication.INSTANCE);
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
	        	reuseableView = inflater.inflate(R.layout.item_home,viewGroup,false);
				
				holder = new Holder();
				holder.imageView = (ImageView) reuseableView.findViewById(R.id.imageView1);
				reuseableView.setTag(holder);
			} else {
				holder = (Holder) reuseableView.getTag();
			}
	        
	        Cultural item = getItem(i);
	        String url = item.ProductPictures.get(0).PictureUrl;
	        ImageLoader.getInstance().displayImage(url, holder.imageView,mOptions);
	        return reuseableView;
	    }
	    
	    private class Holder {
			public ImageView imageView;
		}

	}

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, container, false);
		initUI();
		return mRootView;
	}

	private void initUI() {
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_logo).build();
		fancyCoverFlow = (CoverFlow) mRootView.findViewById(R.id.fancyCoverFlow);
		fancyCoverFlow.isNeedRotate = false;
	    fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				Cultural item = mFancyCoverFlowSampleAdapter.getItem(position);
				List<ProductPicture> mList =  item.ProductPictures;
		        String url = mList.get(Math.min(1, mList.size()-1)).PictureUrl;
		        intent.putExtra("PictureUrl", url);
				startActivity(intent);
			}
		});
	    
	    new HttpManager().loadData(3, R.raw.xinshiqishidai, new OnLoadFinishListener<Cultural>() {

			@Override
			public void onLoad(List<Cultural> mList) {
				if(mList!=null && mList.size()>0){
					mCulturalList = mList;
				}
				mHandler.obtainMessage().sendToTarget();
			}
		});
	}
}
