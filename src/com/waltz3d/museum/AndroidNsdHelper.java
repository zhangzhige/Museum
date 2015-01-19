/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.waltz3d.museum;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.Build;
import android.os.Handler;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public class AndroidNsdHelper {

	private Context mContext;

    private NsdManager mNsdManager;
   
    private NsdManager.ResolveListener mResolveListener;
    
    private NsdManager.DiscoveryListener mDiscoveryListener;

    private static final String SERVICE_TYPE = "_Waltz3D._udp";
    
    private XL_Log log = new XL_Log(AndroidNsdHelper.class);
    
    private String mServiceName = "waltz";

    private NsdServiceInfo mCurrentServiceInfo;
    
    private List<NsdServiceInfo> mServiceInfos = new ArrayList<NsdServiceInfo>();
    
    private List<OnNsdChangeListener> mChangeListeners = new ArrayList<OnNsdChangeListener>();
    
    private Handler mHandler = new Handler(MainApplication.INSTANCE.getMainLooper());
    
    private static AndroidNsdHelper INSTANCE = null;
    
    public static AndroidNsdHelper getInstance() {
		if(INSTANCE == null){
			INSTANCE = new AndroidNsdHelper(MainApplication.INSTANCE);
		}
		return INSTANCE;
	}
    
    public void removeChangeListeners(final OnNsdChangeListener onNsdChangeListener) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				mChangeListeners.remove(onNsdChangeListener);
			}
		});
	}

	public void setmChangeListeners(final OnNsdChangeListener onNsdChangeListener) {
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				mChangeListeners.add(onNsdChangeListener);
			}
		});
	}
	
	public void setCurrentServiceInfo(NsdServiceInfo mCurrentServiceInfo) {
		this.mCurrentServiceInfo = mCurrentServiceInfo;
	}
    
    public List<NsdServiceInfo> getmServiceInfos() {
		return mServiceInfos;
	}

    public AndroidNsdHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        
        initializeNsd();
    }

    public void initializeNsd() {
        initializeResolveListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
            	log.debug("Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
            	log.debug("Service discovery success" + service.getServiceName());
                mNsdManager.resolveService(service, mResolveListener);
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
            	log.debug("service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
            	log.debug("Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            	log.debug("Discovery failed: Error code:" + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            	log.debug("Discovery failed: Error code:" + errorCode);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            	log.debug("Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(final NsdServiceInfo serviceInfo) {
            	log.debug("Resolve Succeeded. " + serviceInfo.getServiceType()+",name="+serviceInfo.getServiceName());
                if (serviceInfo.getServiceType().toLowerCase().contains(mServiceName)) {
                	mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							for(NsdServiceInfo mInfo:mServiceInfos){
								if(mInfo.getHost().getHostAddress().equals(serviceInfo.getHost().getHostAddress()) && mInfo.getPort() == serviceInfo.getPort()){
									//isSame
									return;
								}
							}
							mCurrentServiceInfo = serviceInfo;
							mServiceInfos.add(mCurrentServiceInfo);
							for(OnNsdChangeListener onNsdChangeListener : mChangeListeners){
								onNsdChangeListener.onChange();
							}
						}
					});
                }
            }
        };
    }


    public void discoverServices() {
    	log.debug("discoverServices SERVICE_TYPE="+SERVICE_TYPE);
        stopDiscovery();  // Cancel any existing discovery request
        initializeDiscoveryListener();
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void stopDiscovery() {
    	log.debug("mDiscoveryListener="+mDiscoveryListener);
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } catch(Exception e){
            	e.printStackTrace();
            }
            mDiscoveryListener = null;
        }
    }

    public NsdServiceInfo getChosenServiceInfo() {
		return mCurrentServiceInfo;
	}
    
	public interface OnNsdChangeListener{
		void onChange();
	}
}
