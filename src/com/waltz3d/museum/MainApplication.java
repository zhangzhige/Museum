package com.waltz3d.museum;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.waltz3d.museum.MainApplication.OnNetWorkChangeListener.NetType;

public class MainApplication extends Application {

    XL_Log log = new XL_Log(MainApplication.class);

    public static MainApplication INSTANCE = null;

    private IntentFilter mWifiStateFilter;

    public MainApplication() {
        super();
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
    	CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        
    	SDKInitializer.initialize(this);
        mWifiStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mWifiStateReceiver, mWifiStateFilter);

        initImageLoader();
        
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				AndroidNsdHelper.getInstance().discoverServices();//初始化nsd
			}
		}).start();
    }


    public void killSelf() {
    	AndroidNsdHelper.getInstance().stopDiscovery();
        INSTANCE = null;
    }

    private void initImageLoader() {
        File imageCacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY).threadPoolSize(5)
                .memoryCache(new LruMemoryCache(64 * 1024 * 1024))
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(imageCacheDir))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static boolean isNetAvilible;

    private List<WeakReference<OnNetWorkChangeListener>> mOnNetWorkChangeListenerList = new ArrayList<WeakReference<OnNetWorkChangeListener>>();

    public void addOnNetWorkChangeListener(OnNetWorkChangeListener mListener) {
        mOnNetWorkChangeListenerList.add(new WeakReference<MainApplication.OnNetWorkChangeListener>(mListener));
    }

    public void removeOnNetWorkChangeListener(OnNetWorkChangeListener mListener) {
        mOnNetWorkChangeListenerList.remove(new WeakReference<MainApplication.OnNetWorkChangeListener>(mListener));
    }

    private BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void onReceive(Context context, Intent intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                Util.net_type_changed = true;
                isNetAvilible = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                int networkType = intent.getExtras().getInt(ConnectivityManager.EXTRA_NETWORK_TYPE);
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = cm.getNetworkInfo(networkType);
                log.debug("isNetAvilible=" + isNetAvilible + ",networkType=" + networkType + ",mNetworkInfo=" + mNetworkInfo);
                NetType mtype = NetType.None;
                if (mNetworkInfo != null && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    mtype = NetType.WifiNet;
                } else {
                    mtype = NetType.MobileNet;
                }
                for (WeakReference<OnNetWorkChangeListener> mReference : mOnNetWorkChangeListenerList) {
                    OnNetWorkChangeListener mOnNetWorkChangeListener = mReference.get();
                    if (mOnNetWorkChangeListener != null) {
                        mOnNetWorkChangeListener.onChange(isNetAvilible, mtype);
                    }
                }
            }
        }
    };

    public interface OnNetWorkChangeListener {
        enum NetType {
            MobileNet, WifiNet, None
        }

        /*
         * 网络状态改变通知，首先判断isNetWorkAviliable 如果true则网络可用 后面判断wifi或者移动网络
         */
        void onChange(boolean isNetWorkAviliable, NetType mNetType);
    }
}
