package com.xunlei.common.httpclient;

import org.apache.http.Header;

public abstract class AsyncHttpProxyListener {
	
	public void onSuccess(int statusCode, Header[] headers, String content) 
	{
	}
	
	public void onFailure(Throwable error)
    {
    }
	
	public void onError(int errorCode,String errorDesc)
    {
    }
	
	public void onRetry(int count)
	{
	}
}
