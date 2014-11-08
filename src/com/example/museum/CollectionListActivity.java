package com.example.museum;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CollectionListActivity extends Activity {
	
	private GuideGallery mGuideGallery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collectionlist);
		
		mGuideGallery = (GuideGallery) findViewById(R.id.listview_head);
		mGuideGallery.setAdapter(new ImageAdapter());
	}
	
	
	private class ImageAdapter extends RecyclingPagerAdapter {

		private int[] menu_image_array_common = { R.drawable.collection_1,  R.drawable.collection_2,R.drawable.collection_3,R.drawable.collection_4};
		
		LayoutInflater mInflater;
		
		public ImageAdapter() {
			mInflater = LayoutInflater.from(CollectionListActivity.this);
		}

		public int getCount() {
			return Integer.MAX_VALUE;
		}

		public Integer getItem(int position) {
			return menu_image_array_common[position%menu_image_array_common.length];
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
