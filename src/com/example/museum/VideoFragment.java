package com.example.museum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowSampleAdapter;

public class VideoFragment extends Fragment {
	
	private View mRootView ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.layout_inflate_example, container, false);
		FancyCoverFlow fancyCoverFlow = (FancyCoverFlow) mRootView.findViewById(R.id.fancyCoverFlow);
	    fancyCoverFlow.setAdapter(new FancyCoverFlowSampleAdapter());
		return mRootView;
	}
}
