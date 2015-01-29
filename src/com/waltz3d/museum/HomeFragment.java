package com.waltz3d.museum;

import java.util.List;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.waltz3d.museum.Cultural.ProductPicture;
import com.waltz3d.museum.HttpManager.OnLoadFinishListener;
import com.waltz3d.museum.detail.DetailActivity;

public class HomeFragment extends BaseFragment {
	
	private View mRootView;
	
	private List<Cultural> mCulturalList;
	
	private CoverFlow fancyCoverFlow;
	
	private DisplayImageOptions mOptions;
	
	private FancyCoverFlowSampleAdapter mFancyCoverFlowSampleAdapter;
	
	private ImageView imageView_search;
	
	private ImageView imageView_refresh;
	
	private ProgressDialog mProgressDialog;
	
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
	    public View getView(final int position, View reuseableView, ViewGroup viewGroup) {
	        Holder holder;
	        if (reuseableView == null) {
	        	reuseableView = inflater.inflate(R.layout.item_home,viewGroup,false);
				
				holder = new Holder();
				holder.imageView = (ImageView) reuseableView.findViewById(R.id.imageView1);
				reuseableView.setTag(holder);
			} else {
				holder = (Holder) reuseableView.getTag();
			}
	        
	        final Cultural item = getItem(position);
	        String url = item.ProductPictures.get(0).PictureUrl;
	        ImageLoader.getInstance().displayImage(url, holder.imageView,mOptions);
//	        holder.imageView.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(getActivity(), DetailActivity.class);
//					List<ProductPicture> mList =  item.ProductPictures;
//			        String url = mList.get(Math.min(1, mList.size()-1)).PictureUrl;
//			        intent.putExtra("PictureUrl", url);
//			        intent.putExtra("Id", item.Id);
//			        intent.putExtra("Product3D", item.Product3D);
//					startActivity(intent);
//				}
//			});
	        log.debug("item="+item);
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
		mProgressDialog = new ProgressDialog(getActivity(),android.R.style.Theme_DeviceDefault_Light_Dialog);
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
		        intent.putExtra("Id", item.Id);
		        intent.putExtra("Product3D", item.Product3D);
				startActivity(intent);
			}
		});
		
	    imageView_search = (ImageView) mRootView.findViewById(R.id.imageView_search);
	    imageView_search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), WalthListActivity.class);
				startActivity(intent);
			}
		});
	    
	    imageView_refresh = (ImageView) mRootView.findViewById(R.id.imageView_refresh); 
	    imageView_refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Util.showDialog(mProgressDialog, "正在刷新...");
				new HttpManager().loadDataWithNoCache(3,0,10, new OnLoadFinishListener<Cultural>() {
					
					@Override
					public void onLoad(final List<Cultural> mList) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								Util.dismissDialog(mProgressDialog);
								if(mList == null || mList.size() == 0){
									Util.showToast(getActivity(), "刷新失败，请重试", Toast.LENGTH_LONG);
								}else{
									mFancyCoverFlowSampleAdapter = new FancyCoverFlowSampleAdapter();
									fancyCoverFlow.setAdapter(mFancyCoverFlowSampleAdapter);
								}
							}
						});
					}
				});
			}
		});
	   
	    new HttpManager().loadData(3, 0, 20, R.raw.home_default, new OnLoadFinishListener<Cultural>() {

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
