package com.example.museum;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.example.museum.MainApplication.OnNetWorkChangeListener.NetType;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
        mWifiStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mWifiStateReceiver, mWifiStateFilter);

        initImageLoader();
    }


    public void killSelf() {
        INSTANCE = null;
    }

    private void initImageLoader() {
        File imageCacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY).threadPoolSize(5)
                .memoryCache(new LruMemoryCache(16 * 1024 * 1024))
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
                if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
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
