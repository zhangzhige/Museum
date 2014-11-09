package com.xunlei.common.httpclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.os.Bundle;

import com.example.museum.XL_Log;
import com.xunlei.common.httpclient.handler.AsyncHttpResponseHandler;
import com.xunlei.common.httpclient.handler.BinaryHttpResponseHandlerEx;


/**
 * 
 * 此类包含了重试逻辑。
 * @author lzl
 *
 */
public class AsyncHttpProxy {
	
	private XL_Log log =new XL_Log(AsyncHttpProxy.class);
	
	public static final int LOGIN_REQUEST = 1;
	public static final int LOGINOUT_REQUEST = LOGIN_REQUEST+1;
	public static final int KEEPLIVE_REQUEST = LOGINOUT_REQUEST+1;
	public static final int VERIFYCODE_REQUEST = KEEPLIVE_REQUEST+1;
	public static final int GETUSERINFO_REQUEST = VERIFYCODE_REQUEST+1;
	public static final int PORTAL_REQUEST = GETUSERINFO_REQUEST+1;
	public static final int PINGLIVE_REQUEST = PORTAL_REQUEST+1;
	
	private final static AsyncHttpProxy mInstance = new  AsyncHttpProxy();
	private AsyncHttpClient mClient = new AsyncHttpClient();
	
	private List<AsyncHttpProxyListener>mListenerList = new ArrayList<AsyncHttpProxyListener>();
	
	private AsyncHttpProxy(){
		
	}
	
	private Context mContext =null;
	
	public static AsyncHttpProxy getInstance(){
		return mInstance;
	}
	
	public boolean attachListener(AsyncHttpProxyListener listener){
		if(mListenerList.contains(listener)==true){
			return false;
		}
		mListenerList.add(listener);
		return true;
	}
	
	public void notifyListener(Bundle bundle){
		for(AsyncHttpProxyListener listener:mListenerList){
			if(bundle.getString("type").equalsIgnoreCase("onRetry")){
				listener.onRetry(bundle.getInt("count"));
			}
		}
	}
	
	public boolean detachListener(AsyncHttpProxyListener listener){
		return mListenerList.remove(listener);
	}
	
	public void post(String url,HttpEntity entity, AsyncHttpResponseHandler responseHandler){
		mClient.post(mContext,url, entity, "application/json",responseHandler);
	}
	
	public void get(String url,BinaryHttpResponseHandlerEx responseHandler){
		mClient.get(mContext,url, null, null,responseHandler);
	}



}
