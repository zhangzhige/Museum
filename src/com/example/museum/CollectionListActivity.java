package com.example.museum;

import java.util.List;

import com.example.museum.HttpManager.OnLoadFinishListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class CollectionListActivity extends Activity {

	private GuideGallery mGuideGallery;

	private int categoryids;

	private List<Cultural> mCulturalList;

	private ListView mainlistView;

	private CustomListAdapter mCustomListAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mCulturalList != null && mCulturalList.size() > 0) {
				mCustomListAdapter = new CustomListAdapter(CollectionListActivity.this, mCulturalList);
				mainlistView.setAdapter(mCustomListAdapter);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collectionlist);

		categoryids = getIntent().getIntExtra("categoryids", 5);

		mGuideGallery = (GuideGallery) findViewById(R.id.listview_head);
		mGuideGallery.setAdapter(new ImageAdapter());

		mainlistView = (ListView) findViewById(R.id.mainlistView);
		mainlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(CollectionListActivity.this, DetailActivity.class);
				Cultural item = mCustomListAdapter.getItem(position);
				String url = item.ProductPictures.get(1).PictureUrl;
				intent.putExtra("PictureUrl", url);
				startActivity(intent);
			}
		});

		new HttpManager().loadData(categoryids, categoryids == 5 ? R.raw.xinshiqishidai : R.raw.qingtongshidai, new OnLoadFinishListener<Cultural>() {

			@Override
			public void onLoad(List<Cultural> mList) {
				mCulturalList = mList;
				mHandler.obtainMessage().sendToTarget();
			}
		});
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
