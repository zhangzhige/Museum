package com.example.museum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;

public class HomeFragment extends Fragment {
	
	private View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, container, false);
		initUI();
		return mRootView;
	}

	private void initUI() {
		FancyCoverFlow fancyCoverFlow = (FancyCoverFlow) mRootView.findViewById(R.id.fancyCoverFlow);
	    fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter());
	    fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				startActivity(intent);
			}
		});
	}
}
