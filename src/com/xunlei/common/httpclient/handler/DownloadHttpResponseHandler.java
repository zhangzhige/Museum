/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.xunlei.common.httpclient.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;


import android.os.Message;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}. Receives response body as byte array with a
 * content-type whitelist. (e.g. checks Content-Type against allowed list,
 * Content-length).
 * <p>
 * For example:
 * <p>
 * 
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * String[] allowedTypes = new String[] { &quot;image/png&quot; };
 * client.get(&quot;http://www.example.com/image.png&quot;, new BinaryHttpResponseHandler(allowedTypes) {
 * 	&#064;Override
 * 	public void onSuccess(byte[] imageData) {
 * 		// Successfully got a response
 * 	}
 * 
 * 	&#064;Override
 * 	public void onFailure(Throwable e, byte[] imageData) {
 * 		// Response failed :(
 * 	}
 * });
 * </pre>
 */
public class DownloadHttpResponseHandler extends AsyncHttpResponseHandler {
	// Allow images by default
	private static String[] mAllowedContentTypes = new String[] { "image/jpeg", "image/png", "application/x-javascript" };

	private String mSavePath;

	/**
	 * Creates a new BinaryHttpResponseHandler
	 */
	public DownloadHttpResponseHandler(String savePath) {
		super();
		mSavePath = savePath;
	}

	/**
	 * Creates a new BinaryHttpResponseHandler, and overrides the default
	 * allowed content types with passed String array (hopefully) of content
	 * types.
	 */
	public DownloadHttpResponseHandler(String savePath, String[] allowedContentTypes) {
		this(savePath);
		mAllowedContentTypes = allowedContentTypes;
	}

	//
	// Callbacks to be overridden, typically anonymously
	//

	/**
	 * Fired when a request returns successfully, override to handle in your own
	 * code
	 * 
	 * @param binaryData
	 *            the body of the HTTP response from the server
	 */
	public void onSuccess(String savePath) {
	}

	/**
	 * Fired when a request returns successfully, override to handle in your own
	 * code
	 * 
	 * @param statusCode
	 *            the status code of the response
	 * @param binaryData
	 *            the body of the HTTP response from the server
	 */
	public void onSuccess(int statusCode, String savePath) {
		onSuccess(savePath);
	}

	/**
	 * Fired when a request fails to complete, override to handle in your own
	 * code
	 * 
	 * @param error
	 *            the underlying cause of the failure
	 * @param binaryData
	 *            the response body, if any
	 */
	@SuppressWarnings("deprecation")
	public void onFailure(Throwable error, String savePath) {
		// By default, call the deprecated onFailure(Throwable) for
		// compatibility
		onFailure(error);
	}

	public void addAllowedContentTypes(String contentType) {
		String[] types = new String[mAllowedContentTypes.length + 1];
		System.arraycopy(mAllowedContentTypes, 0, types, 0, mAllowedContentTypes.length);
		types[mAllowedContentTypes.length] = contentType;
	}

	//
	// Pre-processing of messages (executes in background threadpool thread)
	//

	public void sendSuccessMessage(int statusCode, Header[] headers, String savePath) {
		sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { statusCode, headers, savePath }));
	}

	@Override
	@Deprecated
	public void sendFailureMessage(Throwable e, byte[] responseBody) {
		sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { e, responseBody }));
	}

	//
	// Pre-processing of messages (in original calling thread, typically the UI
	// thread)
	//

	protected void handleSuccessMessage(int statusCode, Header[] headers, String savePath) {
//		onSuccess(statusCode, savePath);
		onSuccess(statusCode, headers, savePath);
	}

	protected void handleFailureMessage(Throwable e, String savePath) {
		onFailure(e, savePath);
	}

	// Methods which emulate android's Handler and Message methods
	@Override
	protected void handleMessage(Message msg) {
		Object[] response;
		switch (msg.what) {
		case SUCCESS_MESSAGE:
			response = (Object[]) msg.obj;
			handleSuccessMessage(((Integer) response[0]).intValue(), (Header[])response[1], (String) response[2]);
			break;
		case FAILURE_MESSAGE:
			response = (Object[]) msg.obj;
			handleFailureMessage((Throwable) response[0], (String) response[1]);
			break;
		default:
			super.handleMessage(msg);
			break;
		}
	}

	// Interface to AsyncHttpRequest
	@Override
	public void sendResponseMessage(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		Header[] allHeaders = response.getAllHeaders();
		Header[] contentTypeHeaders = response.getHeaders("Content-Type");
		if (contentTypeHeaders.length != 1) {
			// malformed/ambiguous HTTP Header, ABORT!
			sendFailureMessage(new HttpResponseException(status.getStatusCode(), "None, or more than one, Content-Type Header found!"), mSavePath);
			return;
		}
//		Header contentTypeHeader = contentTypeHeaders[0];
//		boolean foundAllowedContentType = false;
//		for (String anAllowedContentType : mAllowedContentTypes) {
//			if (Pattern.matches(anAllowedContentType, contentTypeHeader.getValue())) {
//				foundAllowedContentType = true;
//			}
//		}
//		foundAllowedContentType = true;
//		if (!foundAllowedContentType) {
//			// Content-Type not in allowed list, ABORT!
//			sendFailureMessage(new HttpResponseException(status.getStatusCode(), "Content-Type not allowed!"), mSavePath);
//			return;
//		}
		
		int responseCode = status.getStatusCode();
		if(responseCode >= 300) {
		    sendFailureMessage(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), mSavePath);
		    return;
		}

		HttpEntity temp = response.getEntity();
		if (null == temp) {
			return;
		}

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		long contentLength = temp.getContentLength();
		long loadedLength = 0;
		try {

			File file = new File(mSavePath);
			File parent = file.getParentFile();
			if(!parent.exists() && !parent.mkdirs()) {
				sendFailureMessage(new IOException("create new path failure!"), parent.getAbsolutePath());
				return;
			}
			if (!file.exists() && !file.createNewFile()) {
				sendFailureMessage(new IOException("create new file failure!"), mSavePath);
				return;
			}
			in = new BufferedInputStream(temp.getContent());
			out = new BufferedOutputStream(new FileOutputStream(mSavePath));
			int length = 8192;
			byte[] buffer = new byte[length];
			while (-1 != (length = in.read(buffer))) {
				out.write(buffer, 0, length);
				loadedLength += length;
				sendProgressChangeMessage(contentLength, loadedLength);
			}
		} catch (IOException e) {
        	e.printStackTrace();
			sendFailureMessage(e, mSavePath);
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				sendFailureMessage(e, mSavePath);
			}
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				sendFailureMessage(e, mSavePath);
			}
		}

		if (status.getStatusCode() < 300) {
		    sendSuccessMessage(status.getStatusCode(), allHeaders, mSavePath);
		}
	}
}
