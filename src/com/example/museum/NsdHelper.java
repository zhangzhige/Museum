package com.example.museum;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NsdHelper {

	XL_Log log = new XL_Log(NsdHelper.class);

	public static final String SERVICE_TYPE = "_http._tcp.";

	public String mServiceName = "Waltz3D";

	NsdManager.DiscoveryListener mDiscoveryListener;

	NsdManager.ResolveListener mResolveListener;

	NsdManager mNsdManager;

	private Context mContext;

	NsdServiceInfo mService;

	public NsdHelper(Context context) {
		mContext = context;
		mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
		initializeResolveListener();
		initializeDiscoveryListener();
	}

	public void discoverServices() {
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }
	
	public NsdServiceInfo getChosenServiceInfo() {
        return mService;
    }
	
    public void tearDown() {
    }
	
	public void initializeDiscoveryListener() {
		mDiscoveryListener = new NsdManager.DiscoveryListener() {

			@Override
			public void onDiscoveryStarted(String regType) {
				log.debug("Service discovery started");
			}

			@Override
			public void onServiceFound(NsdServiceInfo service) {
				log.debug("Service discovery success" + service+"name="+service.getServiceName());
				if (!service.getServiceType().equals(SERVICE_TYPE)) {
					log.debug("Unknown Service Type: " + service.getServiceType());
				} else if (service.getServiceName().equals(mServiceName)) {
					log.debug("Same machine: " + mServiceName);
				} else if (service.getServiceName().contains(mServiceName)) {
					mNsdManager.resolveService(service, mResolveListener);
				}
			}

			@Override
			public void onServiceLost(NsdServiceInfo service) {
				log.debug("service lost" + service);
				if (mService == service) {
					mService = null;
				}
			}

			@Override
			public void onDiscoveryStopped(String serviceType) {
				log.debug("Discovery stopped: " + serviceType);
			}

			@Override
			public void onStartDiscoveryFailed(String serviceType, int errorCode) {
				log.debug("Discovery failed: Error code:" + errorCode);
				mNsdManager.stopServiceDiscovery(this);
			}

			@Override
			public void onStopDiscoveryFailed(String serviceType, int errorCode) {
				log.debug("Discovery failed: Error code:" + errorCode);
				mNsdManager.stopServiceDiscovery(this);
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
			public void onServiceResolved(NsdServiceInfo serviceInfo) {
				log.debug("Resolve Succeeded. " + serviceInfo);
				log.debug("serviceInfo="+serviceInfo.getServiceName()+",type = "+serviceInfo.getServiceType());
				if (serviceInfo.getServiceName().equals(mServiceName)) {
					log.debug("Same IP.");
					return;
				}
				mService = serviceInfo;
			}
		};
	}

}