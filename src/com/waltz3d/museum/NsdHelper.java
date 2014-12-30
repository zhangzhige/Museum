package com.waltz3d.museum;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceInfo.Fields;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;

public class NsdHelper {

	XL_Log log = new XL_Log(NsdHelper.class);
	
	private static final String SERVICE_TYPE = "_Waltz3D._udp.local.";

	public String mServiceName = "Waltz3D";
	
	private static NsdHelper INSTANCE = null;

	private MulticastLock lock;

	private JmDNS jmdns;
	
	private ServiceListener listener;

	private ServiceInfo mCurrentServiceInfo;
	
	private List<OnNsdChangeListener> mChangeListeners = new ArrayList<NsdHelper.OnNsdChangeListener>();
	
	private Handler mHandler = new Handler(MainApplication.INSTANCE.getMainLooper());
	
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

	public void setCurrentServiceInfo(ServiceInfo mCurrentServiceInfo) {
		this.mCurrentServiceInfo = mCurrentServiceInfo;
	}

	private List<ServiceInfo> mServiceInfos;

	public List<ServiceInfo> getmServiceInfos() {
		return mServiceInfos;
	}

	public void setmServiceInfos(List<ServiceInfo> mServiceInfos) {
		this.mServiceInfos = mServiceInfos;
	}

	public static synchronized NsdHelper getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NsdHelper();
		}
		return INSTANCE;
	}

	private void startBonjour() {
		WifiManager wifi = (WifiManager) MainApplication.INSTANCE.getSystemService(android.content.Context.WIFI_SERVICE);
		lock = wifi.createMulticastLock("lock");
		lock.setReferenceCounted(true);
		lock.acquire();
		try {
			jmdns = JmDNS.create();
			listener = new ServiceListener() {
				@Override
				public void serviceResolved(ServiceEvent event) {
					log.debug("serviceResolved QualifiedName=" + event.getInfo().getQualifiedName() + ",ip=" + event.getInfo().getInet4Addresses()[0].getHostAddress().toString() + ",port=" + event.getInfo().getPort());
					if(event != null && event.getInfo() != null){
						mCurrentServiceInfo = event.getInfo();
						mServiceInfos.add(mCurrentServiceInfo);
						log.debug("mServiceInfos="+mServiceInfos.size());
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								for(OnNsdChangeListener onNsdChangeListener : mChangeListeners){
									log.debug("onNsdChangeListener="+onNsdChangeListener);
									onNsdChangeListener.onChange();
								}
							}
						});
					}
				}

				@Override
				public void serviceRemoved(ServiceEvent event) {
					log.debug("serviceRemoved QualifiedName=" + event.getInfo().getQualifiedName() + ",ip=" + event.getInfo().getInet4Addresses()[0].getHostAddress().toString() + ",port="+ event.getInfo().getPort());
				}

				@Override
				public void serviceAdded(ServiceEvent event) {
					log.debug("serviceAdded QualifiedName=" + event.getName() + ",type=" + event.getType());
					jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
				}
			};
			jmdns.addServiceListener(SERVICE_TYPE, listener);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	protected void onStop() {
		if (jmdns != null) {
			if (listener != null) {
				jmdns.removeServiceListener(SERVICE_TYPE, listener);
				listener = null;
			}
			jmdns.unregisterAllServices();
			try {
				jmdns.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jmdns = null;
		}
		lock.release();
	}

	private NsdHelper() {
		startBonjour();
	}

	public ServiceInfo getChosenServiceInfo() {
		return mCurrentServiceInfo;
	}
	
	public interface OnNsdChangeListener{
		void onChange();
	}

}