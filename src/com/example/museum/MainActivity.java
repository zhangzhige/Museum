package com.example.museum;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends android.support.v4.app.FragmentActivity{
	
	private XL_Log log = new XL_Log(MainActivity.class);
	
	public static final int FINISH_THIS = 0;

	private long waitTime = 2000;
	private long touchTime = 0;
	
	private MainView mXlMainView;
	
	public static final int MSG_SHOW_UNICOM3G_TOAST = 1000 + 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log.debug("onCreate=="+this);
		mXlMainView=new MainView(this,savedInstanceState);
		setContentView(mXlMainView);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		log.debug("dispatchKeyEvent >> event.code = " + event.getKeyCode() + ">> event.action = " + event.getAction());
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Util.showToast(this, "再按一次后退键退出应用程序", Toast.LENGTH_SHORT);
				touchTime = currentTime;
			} else {
				releaseAll();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		log.debug("onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		log.debug("onStop");
		super.onStop();
	}

	@Override
	protected void onStart() {
		log.debug("onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		log.debug("onRestart");
		super.onRestart();
		
	}
	
	@Override
	protected void onDestroy() {
		log.debug("-----onDestroy start********************");
		super.onDestroy();
		if(mXlMainView!=null){
			mXlMainView.onDestroy();
		}
	}
	
	/**
	 * 这个是真正释放所有资源，包括kill虚拟机
	 */
	private void releaseAll(){
		//置空静态对象
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
