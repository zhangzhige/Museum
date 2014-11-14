package com.example.museum;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.TargetApi;
import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class DetailActivity extends Activity implements OnClickListener {
	
	XL_Log log = new XL_Log(DetailActivity.class);

	private DisplayImageOptions mOptions;

	private ImageView imageView_detail;

	private NsdHelper mNsdHelper;

	private ChatConnection mConnection;
	
	private String mTestMessage = "<Root><Message ServiceType=\"Waltz3D\" CategoryName=\"Welcome\">11023</Message></Root>";

	private Handler mUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String chatLine = msg.getData().getString("msg");
			log.debug(chatLine);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_logo).build();
		imageView_detail = (ImageView) findViewById(R.id.imageView_detail);

		String url = getIntent().getStringExtra("PictureUrl");
		ImageLoader.getInstance().displayImage(url, imageView_detail, mOptions);

		findViewById(R.id.imageView_back).setOnClickListener(this);
		findViewById(R.id.imageView_action).setOnClickListener(this);

		mNsdHelper = new NsdHelper(this);
		mNsdHelper.discoverServices();
		mConnection = new ChatConnection(mUpdateHandler);
	}

    @Override
    protected void onDestroy() {
        mNsdHelper.tearDown();
        mConnection.tearDown();
        super.onDestroy();
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public void clickConnect() {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        log.debug("clickConnect service = " + service);
        if (service != null) {
        	log.debug("Connecting. service = "+ service.getHost()+",port="+service.getPort());
            mConnection.connectToServer(service.getHost(),service.getPort());
            mConnection.sendMessage(mTestMessage);
        } else {
        	log.debug("No service to connect to!");
            Util.showToast(this, "暂时还未链接到炫立方，请稍后重试", Toast.LENGTH_LONG);
        }
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView_back:
			finish();
			break;
		case R.id.imageView_action:
			clickConnect();
			break;
		default:
			break;
		}
	}
}
