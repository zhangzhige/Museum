package com.example.museum;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayerActivity extends Activity {
	private VideoView vv_video;
	private MediaController mController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		vv_video=(VideoView) findViewById(R.id.vv_video);
		mController=new MediaController(this);
		File file=new File(Util.getSDCardDir(this)+"/"+"DGL031.avi");
		if(file.exists()){
			vv_video.setVideoPath(file.getAbsolutePath());
			vv_video.setMediaController(mController);
			mController.setMediaPlayer(vv_video);
			mController.setPrevNextListeners(new OnClickListener() {
				
				@Override
				public void onClick(View v) {					
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});
		}
	}
}
