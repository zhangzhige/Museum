package com.waltz3d.museum;

import javax.jmdns.ServiceInfo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity implements OnClickListener {
	
	XL_Log log = new XL_Log(DetailActivity.class);

	private DisplayImageOptions mOptions;

	private ImageView imageView_detail;

	private NsdHelper mNsdHelper;

	private ChatConnection mConnection;
	
	private String mTestMessage = "<Root><Message ServiceType=\"Waltz3D\" CategoryName=\"Welcome\">11023</Message></Root>";
	
	private int comFrom = 0;
	
	private String Product3D;

	private Handler mUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String chatLine = msg.getData().getString("msg");
			log.debug(chatLine);
			Util.showToast(DetailActivity.this, "炫立方互动成功", Toast.LENGTH_LONG);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_logo).build();
		imageView_detail = (ImageView) findViewById(R.id.imageView_detail);
		comFrom = getIntent().getIntExtra("comFrom", 0);
		Product3D = getIntent().getStringExtra("Product3D");
		
		if(comFrom == 1){
			TextView textView_title = (TextView) findViewById(R.id.textView_title);
			textView_title.setText("文物列表");
			
			ImageView imageView = (ImageView) findViewById(R.id.imageView_action);
			imageView.setVisibility(View.INVISIBLE);
		}
		String url = getIntent().getStringExtra("PictureUrl");
		ImageLoader.getInstance().displayImage(url, imageView_detail, mOptions);

		findViewById(R.id.imageView_back).setOnClickListener(this);
		findViewById(R.id.imageView_action).setOnClickListener(this);
		if(comFrom != 1){
			int id = getIntent().getIntExtra("Id", 0);
			mTestMessage = mTestMessage.replace("11023", ""+id);
			log.debug("mTestMessage="+mTestMessage);
			mNsdHelper = NsdHelper.getInstance();
			mConnection = new ChatConnection(mUpdateHandler);
		}
	}

    @Override
    protected void onDestroy() {
    	if(mConnection!=null){
    		mConnection.tearDown();
    	}
        super.onDestroy();
    }
    
    private void clickConnect() {
    	ServiceInfo service = mNsdHelper.getChosenServiceInfo();
        log.debug("clickConnect service = " + service);
        if (service != null) {
        	log.debug("Connecting. service = "+ service.getInet4Addresses()[0].getHostAddress().toString()+",port="+service.getPort());
            mConnection.connectToServer(service.getInet4Addresses()[0],service.getPort());
            mConnection.sendMessage(mTestMessage);
        } else {
        	log.debug("No service to connect to!");
            Util.showToast(this, "暂时还未连接到炫立方，请稍后重试", Toast.LENGTH_LONG);
        }
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView_back:
			finish();
			break;
		case R.id.imageView_action:
			if(mNsdHelper != null){
				clickConnect();
			}else{
				Util.showToast(DetailActivity.this, "抱歉，由于你手机版本过低，暂时无法支持炫立方互动。", Toast.LENGTH_LONG);
			}
			break;
		default:
			break;
		}
	}
}
