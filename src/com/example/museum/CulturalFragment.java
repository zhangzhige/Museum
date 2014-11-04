package com.example.museum;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CulturalFragment extends Fragment {
	private View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_cultural, container, false);
		List<String> data = new ArrayList<String>();
		for (int i = 1; i <= 10; i++) {
			data.add(String.format("新石器时代 %d", i));
		}
		CustomAdapter adapter = new CustomAdapter(getActivity(), data);
		ListView listView = (ListView) mRootView.findViewById(R.id.listView1);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view, int pos, long id) {
			}
		});

		return mRootView;
	}
}
