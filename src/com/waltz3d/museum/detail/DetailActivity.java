package com.waltz3d.museum.detail;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.waltz3d.museum.AndroidNsdHelper;
import com.waltz3d.museum.ChatConnection;
import com.waltz3d.museum.DownloadInfo;
import com.waltz3d.museum.R;
import com.waltz3d.museum.TDImagePlayOptionBuilder;
import com.waltz3d.museum.Util;
import com.waltz3d.museum.XL_Log;
import com.waltz3d.museum.detail.To3DHelper.OnloadSuccessListenr;

public class DetailActivity extends Activity implements OnClickListener {
	
	XL_Log log = new XL_Log(DetailActivity.class);

	private DisplayImageOptions mOptions;

	private DetailGifView imageView_detail;

	private AndroidNsdHelper mNsdHelper;

	private ChatConnection mConnection;
	
	private String mTestMessage = "<Root><Message ServiceType=\"Waltz3D\" CategoryName=\"Welcome\">Medias/Welcome/11023</Message></Root>";
	
	private int comFrom = 0;
	
	private String Product3D;
	
	private boolean hasTo3D = false;
	
	private OnloadSuccessListenr mOnloadSuccessListenr = new OnloadSuccessListenr() {
		
		@Override
		public void onLoadSuccess(final List<DownloadInfo> mList) {
			mUpdateHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(mList != null && mList.size() > 0){
						imageView_detail.setResList(mList);
						imageView_detail.start();
					}else{
						hasTo3D = false;
						Util.showToast(DetailActivity.this, "播放3D失败", Toast.LENGTH_SHORT);
					}
				}
			});
		}
	};

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
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        
		mOptions = new TDImagePlayOptionBuilder().setDefaultImage(R.drawable.default_logo).build();
		imageView_detail = (DetailGifView) findViewById(R.id.imageView_detail);
		comFrom = getIntent().getIntExtra("comFrom", 0);
		Product3D = getIntent().getStringExtra("Product3D");
		log.debug("Product3D="+Product3D);
		if(comFrom == 1){
			setTitle("文物列表");
			ImageView imageView = (ImageView) findViewById(R.id.imageView_action);
			imageView.setVisibility(View.INVISIBLE);
		}
		String url = getIntent().getStringExtra("PictureUrl");
		ImageLoader.getInstance().displayImage(url, imageView_detail, mOptions);

		findViewById(R.id.imageView_action).setOnClickListener(this);
		if(comFrom != 1){
			int id = getIntent().getIntExtra("Id", 0);
			mTestMessage = mTestMessage.replace("11023", ""+id);
			log.debug("mTestMessage="+mTestMessage);
			mNsdHelper = AndroidNsdHelper.getInstance();
			mConnection = new ChatConnection(mUpdateHandler);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		if(comFrom == 0){
			getMenuInflater().inflate(R.menu.detail_menu, menu);
		}
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.to_3d:
            	if(!hasTo3D){
            		hasTo3D = true;
            		new To3DHelper(DetailActivity.this, Product3D,mOnloadSuccessListenr);
            	}
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
    	imageView_detail.onStop();
        super.onDestroy();
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN) private void clickConnect() {
    	final NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        log.debug("clickConnect service = " + service);
        if (service != null) {
            mConnection.connectToServer(service.getHost(),service.getPort());
            mConnection.sendMessage(mTestMessage);
        } else {
        	log.debug("No service to connect to!");
            Util.showToast(this, "暂时还未连接到炫立方，请稍后重试", Toast.LENGTH_LONG);
        }
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
