package com.waltz3d.common.httpclient.handler;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Message;

public class BinaryHttpResponseHandlerEx extends BinaryHttpResponseHandler {
	
	public BinaryHttpResponseHandlerEx()
    {
        super();
    }
	
    public void onSuccess(int statusCode,Header[] headers,byte[] binaryData)
    {
    	onSuccess(statusCode,binaryData);
    }
    
    protected void handleSuccessMessage(int statusCode,Header[] headers, byte[] responseBody)
    {
        onSuccess(statusCode, headers,responseBody);
    }
    
    // Methods which emulate android's Handler and Message methods
    @Override
    protected void handleMessage(Message msg)
    {
        Object[] response;
        switch (msg.what)
        {
            case SUCCESS_MESSAGE:
                response = (Object[]) msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (Header []) response[1],(byte[]) response[2]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[]) msg.obj;
                handleFailureMessage((Throwable) response[0], (byte[]) response[1]);
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
    //
    // Pre-processing of messages (executes in background threadpool thread)
    //
    
    public void sendSuccessMessage(int statusCode, Header[] headers, byte [] responseBody)
    {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { Integer.valueOf(statusCode), headers, responseBody }));
    }
    
    @Override
    public void sendResponseMessage(HttpResponse response)
    {
        StatusLine status = response.getStatusLine();
        Header[] contentTypeHeaders = response.getHeaders("Content-Type");
        byte[] responseBody = null;
        
        int responseCode = status.getStatusCode();
        if (responseCode != 200)
        {
            //malformed/ambiguous HTTP Header, ABORT!
            sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getStatusCode() + " " + status.getReasonPhrase()), responseBody);
            return;
        }
//        Header contentTypeHeader = contentTypeHeaders[0];
//        boolean foundAllowedContentType = false;
//        for (String anAllowedContentType : mAllowedContentTypes)
//        {
//            String value = contentTypeHeader.getValue();
//            if (Pattern.matches(anAllowedContentType, value))
//            {
//                foundAllowedContentType = true;
//            }
//        }
//        foundAllowedContentType = true;
//        if (!foundAllowedContentType)
//        {
//            //Content-Type not in allowed list, ABORT!
//            sendFailureMessage(new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"),
//                    responseBody);
//            return;
//        }
        HttpEntity temp = response.getEntity();
        if(temp != null){
        	try
            {
                HttpEntity entity = null;
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toByteArray(entity);
            }
            catch (IOException e)
            {
            	e.printStackTrace();
                sendFailureMessage(e, (byte[]) null);
            }
        }
    
        sendSuccessMessage(status.getStatusCode(),response.getAllHeaders(), responseBody);
    }
    
}
