package com.waltz3d.museum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.xlgson.Gson;
import com.google.xlgson.JsonSyntaxException;
import com.google.xlgson.reflect.TypeToken;
import com.waltz3d.common.httpclient.AsyncHttpProxy;
import com.waltz3d.common.httpclient.handler.AsyncHttpResponseHandler;

public class HttpManager {

	private static final String SearchUrl = "http://tss.waltzcn.com/Plugins/RestApi/API/Product/SearchProducts?loginName=apiuser&loginPassword=123456";
	
	private final static String HostoryVideoUrl = "http://tss.waltzcn.com/content/videos/GetProducts.c";

	private AsyncHttpProxy mHttpProxy = AsyncHttpProxy.getInstance();

	
	public void loadHistoryVideoData(final int rawId,final OnLoadFinishListener<HistoryVideo> mOnLoadFinishListener) {
		final DiskDataCache mDiskDataCache = new DiskDataCache(MainApplication.INSTANCE);
		String cacheData = mDiskDataCache.loadDataFromDiskImpl(HostoryVideoUrl);
		if (cacheData != null && cacheData.length() > 0) {

		} else if (rawId != 0) {
			cacheData = openRawResource(rawId);
		}
		if (cacheData != null && cacheData.length() > 0) {
			Gson gson = new Gson();
			try {
				List<HistoryVideo> mList = gson.fromJson(cacheData,
						new TypeToken<List<HistoryVideo>>() {
						}.getType());
				mOnLoadFinishListener.onLoad(mList);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mHttpProxy.get(HostoryVideoUrl, new AsyncHttpResponseHandler(){
			public void onSuccess(int statusCode, Header[] headers,String content) {
				Log.d("TAG", "content=" + content);
				Gson gson = new Gson();
				List<HistoryVideo> mList = null;
				try {
					mList = gson.fromJson(content, new TypeToken<List<HistoryVideo>>() {}.getType());
					mDiskDataCache.saveCacheData(content, HostoryVideoUrl);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mOnLoadFinishListener.onLoad(mList);
			}

			public void onFailure(Throwable error, String content) {
				mOnLoadFinishListener.onLoad(null);
			}
		});
	}
	
	
	/**
	 * 
	 * @param <T>
	 * @param categoryids
	 *            2:过场动画 3：首页 13 历史视频 5 新石器时代 6 青铜时代
	 */
	public void loadData(int categoryids, int rawId,
			final OnLoadFinishListener<Cultural> mOnLoadFinishListener) {
		JSONObject jsonRequObj = new JSONObject();
		try {
			jsonRequObj.put("pageindex", 0);
			jsonRequObj.put("pagesize", 20);
			jsonRequObj.put("categoryids", categoryids);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final String jsonContent = jsonRequObj.toString();
		final DiskDataCache mDiskDataCache = new DiskDataCache(
				MainApplication.INSTANCE);
		String cacheData = mDiskDataCache.loadDataFromDiskImpl(jsonContent);
		if (cacheData != null && cacheData.length() > 0) {

		} else if (rawId != 0) {
			cacheData = openRawResource(rawId);
		}
		if (cacheData != null && cacheData.length() > 0) {
			Gson gson = new Gson();
			try {
				List<Cultural> mList = gson.fromJson(cacheData,
						new TypeToken<List<Cultural>>() {
						}.getType());
				mOnLoadFinishListener.onLoad(mList);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ByteArrayEntity byteEntity = new ByteArrayEntity(jsonContent.getBytes());

		mHttpProxy.post(SearchUrl, byteEntity, new AsyncHttpResponseHandler() {

			public void onSuccess(int statusCode, Header[] headers,String content) {
				Log.d("TAG", "content=" + content);
//				try {
//					JSONArray mJsonArray = new JSONArray(content);
//					for(int i = 0,size = mJsonArray.length();i<size;i++){
//						JSONObject mJsonObject = mJsonArray.getJSONObject(i);
//						Iterator<String> mIterator = mJsonObject.keys();
//						while (mIterator.hasNext()) { 
//							Object mObject = mJsonObject.get(mIterator.next());
//						} 
//						
//					}
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
				
				Gson gson = new Gson();
				List<Cultural> mList = null;
				try {
					mList = gson.fromJson(content, new TypeToken<List<Cultural>>() {
					}.getType());
					mDiskDataCache.saveCacheData(content, jsonContent);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mOnLoadFinishListener.onLoad(mList);
			}

			public void onFailure(Throwable error, String content) {
				mOnLoadFinishListener.onLoad(null);
			}
		});
	}

	public static String openRawResource(int resourceid) {
		InputStream is = MainApplication.INSTANCE.getResources()
				.openRawResource(resourceid);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader;
			try {
				reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String jsonString = writer.toString();
		return jsonString;
	}

	public static void saveDiskObject(Context context, String fileName,
			Object object) {
		ObjectOutputStream out = null;
		try {
			File file = new File(context.getFilesDir(), fileName);
			FileOutputStream outstream = new FileOutputStream(file);
			out = new ObjectOutputStream(outstream);
			out.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Object loadDiskObject(Context context, String fileName) {
		FileInputStream in = null;
		ObjectInputStream s = null;
		Object object = null;
		try {
			in = context.openFileInput(fileName);
			s = new ObjectInputStream(in);
			object = s.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	public interface OnLoadFinishListener<T> {
		void onLoad(List<T> mList);
	}
}
