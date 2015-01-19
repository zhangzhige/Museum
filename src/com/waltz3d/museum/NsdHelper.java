//package com.waltz3d.museum;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.jmdns.JmDNS;
//import javax.jmdns.ServiceEvent;
//import javax.jmdns.ServiceInfo;
//import javax.jmdns.ServiceListener;
//
//import android.net.wifi.WifiManager;
//import android.net.wifi.WifiManager.MulticastLock;
//import android.os.Handler;
//
//public class NsdHelper {
//
//	XL_Log log = new XL_Log(NsdHelper.class);
//	
//	private static final String SERVICE_TYPE = "_Waltz3D._udp.local.";
//
//	public String mServiceName = "Waltz3D";
//	
//	private static NsdHelper INSTANCE = null;
//
//	private MulticastLock lock;
//
//	private JmDNS jmdns;
//	
//	private ServiceListener listener;
//
//	private ServiceInfo mCurrentServiceInfo;
//	
//	private List<OnNsdChangeListener> mChangeListeners = new ArrayList<NsdHelper.OnNsdChangeListener>();
//	
//	private Handler mHandler = new Handler(MainApplication.INSTANCE.getMainLooper());
//	
//	public void removeChangeListeners(final OnNsdChangeListener onNsdChangeListener) {
//		mHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				mChangeListeners.remove(onNsdChangeListener);
//			}
//		});
//	}
//
//	public void setmChangeListeners(final OnNsdChangeListener onNsdChangeListener) {
//		mHandler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				mChangeListeners.add(onNsdChangeListener);
//			}
//		});
//	}
//
//	public void setCurrentServiceInfo(ServiceInfo mCurrentServiceInfo) {
//		this.mCurrentServiceInfo = mCurrentServiceInfo;
//	}
//
//	private List<ServiceInfo> mServiceInfos = new ArrayList<ServiceInfo>();
//
//	public List<ServiceInfo> getmServiceInfos() {
//		return mServiceInfos;
//	}
//
//	public static synchronized NsdHelper getInstance() {
//		if (INSTANCE == null) {
//			INSTANCE = new NsdHelper();
//		}
//		return INSTANCE;
//	}
//
//	private void startBonjour() {
//		WifiManager wifi = (WifiManager) MainApplication.INSTANCE.getSystemService(android.content.Context.WIFI_SERVICE);
//		lock = wifi.createMulticastLock("lock");
//		lock.setReferenceCounted(true);
//		lock.acquire();
//		try {
//			jmdns = JmDNS.create();
//			listener = new ServiceListener() {
//				@Override
//				public void serviceResolved(final ServiceEvent event) {
//					if(event != null && event.getInfo() != null){
//						log.debug("serviceResolved ServiceEvent="+event.getInfo().getName()+",port="+event.getInfo().getPort());
//						if(event.getInfo().getName().contains("Waltz3D")){
//							mHandler.post(new Runnable() {
//								
//								@Override
//								public void run() {
//									mCurrentServiceInfo = event.getInfo();
//									mServiceInfos.add(mCurrentServiceInfo);
//									log.debug("serviceResolved QualifiedName=" + event.getInfo().getName() + ",ip=" + event.getInfo().getInet4Addresses()[0].getHostAddress().toString() + ",port=" + event.getInfo().getPort());
//									log.debug("mServiceInfos="+mServiceInfos.size());
//									
//									for(OnNsdChangeListener onNsdChangeListener : mChangeListeners){
//										log.debug("onNsdChangeListener="+onNsdChangeListener);
//										onNsdChangeListener.onChange();
//									}
//								}
//							});
//						}
//					}
//				}
//
//				@Override
//				public void serviceRemoved(ServiceEvent event) {
//					log.debug("serviceRemoved QualifiedName=" + event.getInfo().getQualifiedName() + ",ip=" + event.getInfo().getInet4Addresses()[0].getHostAddress().toString() + ",port="+ event.getInfo().getPort());
//				}
//
//				@Override
//				public void serviceAdded(final ServiceEvent event) {
//					log.debug("serviceAdded QualifiedName=" + event.getName() + ",type=" + event.getType()+",event="+event.getInfo().getPort());
//					if(event != null && event.getInfo() != null){
//						jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
//					}
//				}
//			};
//			log.debug("startBonjour SERVICE_TYPE");
//			jmdns.addServiceListener(SERVICE_TYPE, listener);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//	}
//
//	protected void onStop() {
//		if (jmdns != null) {
//			if (listener != null) {
//				jmdns.removeServiceListener(SERVICE_TYPE, listener);
//				listener = null;
//			}
//			jmdns.unregisterAllServices();
//			try {
//				jmdns.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			jmdns = null;
//		}
//		lock.release();
//	}
//
//	private NsdHelper() {
//		startBonjour();
//	}
//
//	public ServiceInfo getChosenServiceInfo() {
//		return mCurrentServiceInfo;
//	}
//	
//	public interface OnNsdChangeListener{
//		void onChange();
//	}
//
//}