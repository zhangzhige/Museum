package com.waltz3d.museum;

import java.util.List;

import com.waltz3d.museum.FootListView.FootStatus;
import com.waltz3d.museum.FootListView.OnLoadMoreListenr;
import com.waltz3d.museum.HttpManager.OnLoadFinishListener;
import com.waltz3d.museum.detail.DetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

public class CollectionListActivity extends Activity {

	private GuideGallery mGuideGallery;

	private int categoryids;

	private FootListView mainlistView;

	private CustomListAdapter mCustomListAdapter;
	
	private TextView textView_listhead;
	
	private static final int DEFINE_SIZE = 5;
	
	private SwipeRefreshLayout swipeLayout;

	private Handler mHandler = new Handler();

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collectionlist);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        
		categoryids = getIntent().getIntExtra("categoryids", 5);
		textView_listhead =(TextView) findViewById(R.id.textView_listhead);
		textView_listhead.setText(categoryids == 5?"新石器时代":"青铜时代");
		mGuideGallery = (GuideGallery) findViewById(R.id.listview_head);
		mGuideGallery.setAdapter(new ImageAdapter());
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeLayout.setColorSchemeResources(R.color.holo_red_light, R.color.holo_green_light, R.color.holo_blue_bright, R.color.holo_orange_light);  
	  
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				loadData(0,false);	
			}
		});
		swipeLayout.setRefreshing(true);
		
		mainlistView = (FootListView) findViewById(R.id.mainlistView);
		mainlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(CollectionListActivity.this, DetailActivity.class);
				Cultural item = mCustomListAdapter.getItem(position);
				if(item != null){
					String url = item.ProductPictures.get(Math.min(1, item.ProductPictures.size() - 1)).PictureUrl;
					intent.putExtra("PictureUrl", url);
					intent.putExtra("comFrom", 1);
					startActivity(intent);
				}
				
			}
		});
		
		mainlistView.setOnLoadMoreListenr(new OnLoadMoreListenr() {
			
			@Override
			public void onLoadData() {
				int pageIndex = mCustomListAdapter.getCount()/DEFINE_SIZE;
				loadData(pageIndex,true);	
			}
		});
		
		mCustomListAdapter = new CustomListAdapter(CollectionListActivity.this);
		mainlistView.setAdapter(mCustomListAdapter);
		
		loadData(0,false);
	}
	
	private void loadData(int pageIndex,final boolean isAppend){
		Log.d("loadData", "pageIndex="+pageIndex+",isAppend="+isAppend);
		new HttpManager().loadDataWithNoCache(categoryids, pageIndex, DEFINE_SIZE, new OnLoadFinishListener<Cultural>() {

			@Override
			public void onLoad(final List<Cultural> mList) {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(mList != null && mList.size() > 0){
							mCustomListAdapter.refreshData(mList, isAppend);
							if(mList.size() < DEFINE_SIZE){
								mainlistView.setFootStatus(FootStatus.Gone);
							}else{
								mainlistView.setFootStatus(FootStatus.Common);
							}
							
						}else{//拉取数据失败，则设置不可见
							mainlistView.setFootStatus(FootStatus.Gone);
						}
						swipeLayout.setRefreshing(false); 
					}
				});
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mGuideGallery!=null){
			mGuideGallery.onResume();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mGuideGallery!=null){
			mGuideGallery.onStop();
		}
	}
	
	private class ImageAdapter extends RecyclingPagerAdapter {

		private int[] menu_image_array_common = { R.drawable.collection_1, R.drawable.collection_2, R.drawable.collection_3, R.drawable.collection_4 };

		LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = LayoutInflater.from(CollectionListActivity.this);
		}

		public int getCount() {
			return Integer.MAX_VALUE;
		}

		public Integer getItem(int position) {
			return menu_image_array_common[position % menu_image_array_common.length];
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
			final Integer mItem = getItem(position);
			n.gallery_image.setImageResource(mItem);
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
			return menu_image_array_common.length;
		}
	}
}
