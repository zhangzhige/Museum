package com.example.museum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends BaseAdapter {
	private final Context context;
	private final List<String> items;
	private final Map<View, Map<Integer, View>> cache = new HashMap<View, Map<Integer, View>>();

	public CustomAdapter(Context context, List<String> items) {
		this.context = context;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		TextView tv;
		ImageView iv;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.cultural_item, parent, false);
		}
		Map<Integer, View> itemMap = cache.get(v);
		if (itemMap == null) {
			itemMap = new HashMap<Integer, View>();
			tv = (TextView) v.findViewById(R.id.textView1);
			iv = (ImageView) v.findViewById(R.id.imageView1);
			itemMap.put(R.id.textView1, tv);
			itemMap.put(R.id.imageView1, iv);
			cache.put(v, itemMap);
		} else {
			tv = (TextView) itemMap.get(R.id.textView1);
			iv = (ImageView) itemMap.get(R.id.imageView1);
		}
		final String item = (String) getItem(position);
		tv.setText(item);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(context, String.format("Image clicked: %s", item), Toast.LENGTH_SHORT).show();
			}
		});
		return v;
	}
}
