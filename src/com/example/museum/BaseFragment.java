package com.example.museum;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {

    XL_Log log = new XL_Log(BaseFragment.class);
    
    protected View mRootView;
   
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(mRootView!=null){
    		mRootView = onlyOneOnCreateView(mRootView);
    	}else{
    		mRootView = onCreateContentView(inflater,container,savedInstanceState);
    	}
    	return mRootView;
    }

    protected abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	/**
     * 保证fragment的oncreateView只被初始化一次，避免资源浪费
     *
     * @param view
     */
    protected View onlyOneOnCreateView(View rootView) {
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }
}
