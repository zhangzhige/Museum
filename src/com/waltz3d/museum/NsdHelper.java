package com.waltz3d.museum;

import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;

public class NsdHelper {

	XL_Log log = new XL_Log(NsdHelper.class);
	
	private static final String SERVICE_TYPE = "_http._tcp.local.";

	public String mServiceName = "Waltz3D";
	
	private static NsdHelper INSTANCE = null;

	private MulticastLock lock;

	private JmDNS jmdns;
	private ServiceListener listener;

	private ServiceInfo mServiceInfo;

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
			jmdns.addServiceListener(SERVICE_TYPE, listener = new ServiceListener() {
				@Override
				public void serviceResolved(ServiceEvent event) {
					log.debug("serviceResolved QualifiedName=" + event.getInfo().getQualifiedName() + ",ip=" + event.getInfo().getInet4Addresses()[0].getHostAddress().toString() + ",port="
							+ event.getInfo().getPort());
					if(event != null && event.getInfo() != null && event.getInfo().getQualifiedName().contains(mServiceName)){
						mServiceInfo = event.getInfo();
						
						onStop();
					}
				}

				@Override
				public void serviceRemoved(ServiceEvent event) {
					log.debug("serviceRemoved QualifiedName=" + event.getInfo().getQualifiedName() + ",ip=" + event.getInfo().getInet4Addresses()[0].getHostAddress().toString() + ",port="
							+ event.getInfo().getPort());
				}

				@Override
				public void serviceAdded(ServiceEvent event) {
					log.debug("serviceAdded QualifiedName=" + event.getName() + ",type=" + event.getType());
					jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
				}
			});
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
		return mServiceInfo;
	}

}