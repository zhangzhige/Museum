package com.pixplicity.multiviewpager;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.waltz3d.museum.HistoryVideo;
import com.waltz3d.museum.MainApplication;
import com.waltz3d.museum.R;
import com.waltz3d.museum.TDImagePlayOptionBuilder;
import com.waltz3d.museum.Util;
import com.waltz3d.museum.VideoPlayerActivity;
import com.waltz3d.museum.detail.DetailActivity;
import com.waltz3d.museum.detail.To3DHelper;

public class SimpleAdapter extends RecyclingPagerAdapter {

	private final LayoutInflater inflater;
	
	private List<HistoryVideo> mCulturalList;
	
	private DisplayImageOptions mOptions;
	
	private Context mContext;

	public SimpleAdapter(Context context,List<HistoryVideo> mList) {
		inflater = LayoutInflater.from(MainApplication.INSTANCE);
		this.mContext = context;
		this.mCulturalList = mList;
		
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(
				R.drawable.default_video_logo).build();
	}

	public HistoryVideo getItem(int i) {
		return mCulturalList.get(i);
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view != null) {
			holder = (ViewHolder) view.getTag();
		} else {
			view = inflater.inflate(R.layout.item_video, container, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		}
		holder.imageView_play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Util.isWifiNet(mContext)){
					Intent intent = new Intent(mContext, VideoPlayerActivity.class);
					HistoryVideo item = getItem(position);
					String url = item.url;
					intent.putExtra("url", url);
					mContext.startActivity(intent);
        		}else{
        			AlertDialog.Builder mBuidler = new AlertDialog.Builder(mContext);
        			mBuidler.setTitle("网络环境提示");
        			mBuidler.setMessage("检测到您的网络环境是移动网络，播放视频需要耗费较大流量，建议在wifi环境下播放");
        			mBuidler.setNegativeButton("继续播放", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(mContext, VideoPlayerActivity.class);
							HistoryVideo item = getItem(position);
							String url = item.url;
							intent.putExtra("url", url);
							mContext.startActivity(intent);
						}
					});
        			mBuidler.setPositiveButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
        			mBuidler.create().show();
        		}
				
				
			}
		});
		HistoryVideo item = getItem(position);
		String url = item.photo;
		ImageLoader.getInstance().displayImage(url, holder.imageView,mOptions);

		return view;
	}

	@Override
	public int getCount() {
		return mCulturalList.size();
	}

	private static class ViewHolder {
		public ImageView imageView;
		public ImageView imageView_play;

		public ViewHolder(View view) {
			imageView = (ImageView) view.findViewById(R.id.imageView1);
			imageView_play = (ImageView) view.findViewById(R.id.imageView_play);
		}
	}
}
