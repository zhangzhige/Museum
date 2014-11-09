package com.example.museum;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class DetailActivity extends Activity implements OnClickListener{
	
	private DisplayImageOptions mOptions;
	
	private ImageView imageView_detail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_banner_image).build();
		imageView_detail = (ImageView) findViewById(R.id.imageView_detail);
		
		String url = getIntent().getStringExtra("PictureUrl");
		ImageLoader.getInstance().displayImage(url, imageView_detail,mOptions);
		
		findViewById(R.id.imageView_back).setOnClickListener(this);
		findViewById(R.id.imageView_action).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView_back:
			finish();
			break;
		case R.id.imageView_action:
			break;
		default:
			break;
		}
	}
}
