package com.example.museum;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.museum.Cultural.ProductSpecificationAttribute;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CustomListAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<Cultural> mCulturals;
	private LayoutInflater inflater;
	
	public int position;
	
	public int offset;
	
	private DisplayImageOptions mOptions;
	
	public CustomListAdapter(Context context,List<Cultural> list) {
		super();
		this.context = context;
		this.mCulturals = list;
		this.inflater = LayoutInflater.from(context);
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_logo).build();
	}

	@Override
	public int getCount() {
		return mCulturals.size();
	}

	@Override
	public Cultural getItem(int position) {
		return mCulturals.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_share_new,parent,false);
			
			holder = new Holder();
			holder.imageView_file_logo = (ImageView) convertView.findViewById(R.id.imageView_file_logo);
			holder.textView_name_key=(TextView) convertView.findViewById(R.id.textView_name_key);
			holder.textView_name_value=(TextView) convertView.findViewById(R.id.textView_name_value);
			
			holder.textView_location_key=(TextView) convertView.findViewById(R.id.textView_location_key);
			holder.textView_location_value=(TextView) convertView.findViewById(R.id.textView_location_value);
			
			holder.textView_disability_key=(TextView) convertView.findViewById(R.id.textView_disability_key);
			holder.textView_textView_disability_value=(TextView) convertView.findViewById(R.id.textView_textView_disability_value);
			
			holder.textView_level_key=(TextView) convertView.findViewById(R.id.textView_level_key);
			holder.textView_level_value=(TextView) convertView.findViewById(R.id.textView_level_value);
			
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		Cultural mItem = getItem(position);
	    String url = mItem.ProductPictures.get(0).PictureUrl;
	    ImageLoader.getInstance().displayImage(url,holder.imageView_file_logo,mOptions);
	        
		List<ProductSpecificationAttribute> mProductSpecificationAttributes = mItem.ProductSpecificationAttributes;
		if(mProductSpecificationAttributes!=null && mProductSpecificationAttributes.size()>0){
			try {
				holder.textView_name_key.setText(mProductSpecificationAttributes.get(0).Name);
				holder.textView_name_value.setText(mProductSpecificationAttributes.get(0).Value);
				
				holder.textView_location_key.setText(mProductSpecificationAttributes.get(1).Name);
				holder.textView_location_value.setText(mProductSpecificationAttributes.get(1).Value);
				
				holder.textView_disability_key.setText(mProductSpecificationAttributes.get(2).Name);
				holder.textView_textView_disability_value.setText(mProductSpecificationAttributes.get(2).Value);
				
				holder.textView_level_key.setText(mProductSpecificationAttributes.get(3).Name);
				holder.textView_level_value.setText(mProductSpecificationAttributes.get(3).Value);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

	private class Holder {
		public ImageView imageView_file_logo;
		public ImageView imageView_menu;
		public TextView textView_name_key,textView_name_value;
		public TextView textView_location_key,textView_location_value;
		public TextView textView_disability_key,textView_textView_disability_value;
		public TextView textView_level_key,textView_level_value;

	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
