package com.waltz3d.museum;

import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceInfo;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.waltz3d.museum.NsdHelper.OnNsdChangeListener;

public class WalthListActivity extends Activity {
	
	private ListView mainlistView;
	
	private TextView textView_empty;
	
	private SimpleCustomAdapter mSimpleCustomAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waltzlist);
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);

		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		View customNav = LayoutInflater.from(this).inflate(R.layout.actionbar_title, null);
		mActionBar.setCustomView(customNav, lp);
		mActionBar.setDisplayShowCustomEnabled(true);
		
		textView_empty = (TextView) findViewById(R.id.textView_empty);
		
		mainlistView = (ListView) findViewById(R.id.mainlistView);
		mainlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ServiceInfo mServiceInfo = mSimpleCustomAdapter.getItem(position);
				if(mServiceInfo != null){
					NsdHelper.getInstance().setCurrentServiceInfo(mServiceInfo);
					Util.showToast(WalthListActivity.this, "当前已选中："+mServiceInfo.getQualifiedName(), Toast.LENGTH_SHORT);
				}
			}
		});
		
		mSimpleCustomAdapter = new SimpleCustomAdapter();
		mainlistView.setAdapter(mSimpleCustomAdapter);
		
		List<ServiceInfo> mServiceInfos = NsdHelper.getInstance().getmServiceInfos();
		if(mServiceInfos == null || mServiceInfos.size() == 0){
			textView_empty.setVisibility(View.VISIBLE);
		}else{
			mSimpleCustomAdapter.refreshData(mServiceInfos);
			textView_empty.setVisibility(View.GONE);
		}
		NsdHelper.getInstance().setmChangeListeners(mOnNsdChangeListener);
	}
	
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
	
	private OnNsdChangeListener mOnNsdChangeListener = new OnNsdChangeListener(){

		@Override
		public void onChange() {
			List<ServiceInfo> mServiceInfos = NsdHelper.getInstance().getmServiceInfos();
			if(mServiceInfos != null && mServiceInfos.size() > 0){
				mSimpleCustomAdapter.refreshData(mServiceInfos);
				textView_empty.setVisibility(View.GONE);
			}else{
				textView_empty.setVisibility(View.VISIBLE);
			}
		}
		
	};
	
	protected void onDestroy() {
		super.onDestroy();
		NsdHelper.getInstance().removeChangeListeners(mOnNsdChangeListener);
	};
	
	private class SimpleCustomAdapter extends BaseAdapter {
		
		private List<ServiceInfo> mServiceInfos;
		
		private LayoutInflater inflater;

		public SimpleCustomAdapter() {
			this.mServiceInfos = new ArrayList<ServiceInfo>();
			this.inflater = LayoutInflater.from(WalthListActivity.this);
		}

		public void refreshData(List<ServiceInfo> items){
			mServiceInfos = items;
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return mServiceInfos.size();
		}

		@Override
		public ServiceInfo getItem(int position) {
			return mServiceInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return getItem(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_waltz_list, parent, false);
				holder = new Holder();
				holder.textView_name = (TextView) convertView.findViewById(R.id.textView_name);
				convertView.setTag(holder);

			} else {
				holder = (Holder) convertView.getTag();
			}
			ServiceInfo mItem = getItem(position);
			holder.textView_name.setText(mItem.getQualifiedName());
			return convertView;
		}
		
		private class Holder {
			public TextView textView_name;

		}
	}
}
