package com.example.museum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CulturalFragment extends BaseFragment {

	@Override
	public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_cultural, container, false);
		mRootView.findViewById(R.id.xinshiqi_layout).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CollectionListActivity.class);
				intent.putExtra("categoryids", 5);
				startActivity(intent);
			}
		});
		
		mRootView.findViewById(R.id.qingtong_layout).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CollectionListActivity.class);
				intent.putExtra("categoryids", 6);
				startActivity(intent);
			}
		});
		return mRootView;
	}
}
