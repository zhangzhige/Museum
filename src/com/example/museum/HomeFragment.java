package com.example.museum;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.museum.HttpManager.OnLoadFinishListener;
import com.google.xlgson.Gson;
import com.google.xlgson.JsonSyntaxException;
import com.google.xlgson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xunlei.common.httpclient.AsyncHttpProxy;
import com.xunlei.common.httpclient.handler.AsyncHttpResponseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class HomeFragment extends Fragment {
	
	private View mRootView;
	
	private AsyncHttpProxy mHttpProxy = AsyncHttpProxy.getInstance();
	
	private List<Cultural> mCulturalList;
	
	private FancyCoverFlow fancyCoverFlow;
	
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
	
	public class FancyCoverFlowSampleAdapter extends FancyCoverFlowAdapter {

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
	    public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
	        ImageView imageView = null;

	        if (reuseableView != null) {
	            imageView = (ImageView) reuseableView;
	        } else {
	            imageView = new ImageView(viewGroup.getContext());
	            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	            imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(300, 400));

	        }
	        Cultural item = getItem(i);
	        String url = item.ProductPictures.get(0).PictureUrl;
	        ImageLoader.getInstance().displayImage(url, imageView,mOptions);
	        return imageView;
	    }
	}
	
	private String SearchUrl = "http://tss.waltzcn.com/Plugins/RestApi/API/Product/SearchProducts?loginName=apiuser&loginPassword=123456";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, container, false);
		initUI();
		return mRootView;
	}

	private void initUI() {
		
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_banner_image).build();
		
		fancyCoverFlow = (FancyCoverFlow) mRootView.findViewById(R.id.fancyCoverFlow);
	    fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				Cultural item = mFancyCoverFlowSampleAdapter.getItem(position);
		        String url = item.ProductPictures.get(1).PictureUrl;
		        intent.putExtra("PictureUrl", url);
				startActivity(intent);
			}
		});
	    
	    new HttpManager().loadData(3, 0, new OnLoadFinishListener<Cultural>() {

			@Override
			public void onLoad(List<Cultural> mList) {
				mCulturalList = mList;
				mHandler.obtainMessage().sendToTarget();
			}
		});
	}
}
