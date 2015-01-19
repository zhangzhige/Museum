package com.waltz3d.museum;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
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

	private XL_Log log = new XL_Log(HttpManager.class);

	public void loadHistoryVideoData(final int rawId, final OnLoadFinishListener<HistoryVideo> mOnLoadFinishListener) {
		final DiskDataCache mDiskDataCache = new DiskDataCache(MainApplication.INSTANCE);
		String cacheData = mDiskDataCache.loadDataFromDiskImpl(HostoryVideoUrl);
		if (cacheData != null && cacheData.length() > 0) {

		} else if (rawId != 0) {
			cacheData = openRawResource(rawId);
		}
		if (cacheData != null && cacheData.length() > 0) {
			Gson gson = new Gson();
			try {
				List<HistoryVideo> mList = gson.fromJson(cacheData, new TypeToken<List<HistoryVideo>>() {
				}.getType());
				mOnLoadFinishListener.onLoad(mList);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mHttpProxy.get(HostoryVideoUrl, new AsyncHttpResponseHandler() {
			public void onSuccess(int statusCode, Header[] headers, String content) {
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
	 *            3 首页 5 新石器时代 6 青铜时代
	 */
	public void loadData(final int categoryids, final int pageIndex,final int pageSize,final int rawId, final OnLoadFinishListener<Cultural> mOnLoadFinishListener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				String postData = "pageSize=" + pageSize + "&categoryids=" + categoryids + "&pageIndex=" + pageIndex;
				final DiskDataCache mDiskDataCache = new DiskDataCache(MainApplication.INSTANCE);
				String cacheData = mDiskDataCache.loadDataFromDiskImpl(postData);
				if ((cacheData == null || cacheData.length() == 0) && rawId != 0) {
					cacheData = openRawResource(rawId);
				}
				if (cacheData != null && cacheData.length() > 0) {
					Gson gson = new Gson();
					try {
						List<Cultural> mList = gson.fromJson(cacheData, new TypeToken<List<Cultural>>() {
						}.getType());
						mOnLoadFinishListener.onLoad(mList);
					} catch (JsonSyntaxException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				String responseData = getNetData(postData);
				Log.d("loadDataTAG", "content=" + responseData);
				if (responseData != null && responseData.length() > 0) {
					Gson gson = new Gson();
					List<Cultural> mList = null;
					try {
						mList = gson.fromJson(responseData, new TypeToken<List<Cultural>>() {}.getType());
						log.debug("mList=" + mList.size());
						mOnLoadFinishListener.onLoad(mList);
						mDiskDataCache.saveCacheData(responseData, postData);
					} catch (Exception e) {
						mOnLoadFinishListener.onLoad(null);
						e.printStackTrace();
					}
				} else {
					mOnLoadFinishListener.onLoad(null);
				}
			}
		}).start();
	}

	public void loadDataWithNoCache(final int categoryids, final int pageIndex,final int pageSize,final OnLoadFinishListener<Cultural> mOnLoadFinishListener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String postData = "pageSize=" + pageSize + "&categoryids=" + categoryids + "&pageIndex=" + pageIndex;
				final DiskDataCache mDiskDataCache = new DiskDataCache(MainApplication.INSTANCE);
				String responseData = getNetData(postData);
				if (responseData != null && responseData.length() > 0) {
					Gson gson = new Gson();
					List<Cultural> mList = null;
					try {
						mList = gson.fromJson(responseData, new TypeToken<List<Cultural>>() {}.getType());
						log.debug("mList=" + mList.size());
						mOnLoadFinishListener.onLoad(mList);
						mDiskDataCache.saveCacheData(responseData, postData);
					} catch (Exception e) {
						mOnLoadFinishListener.onLoad(null);
						e.printStackTrace();
					}
				} else {
					mOnLoadFinishListener.onLoad(null);
				}
			}
		}).start();
	}

	public static String openRawResource(int resourceid) {
		InputStream is = null;
		Writer writer = null;
		try {
			is = MainApplication.INSTANCE.getResources().openRawResource(resourceid);
			writer = new StringWriter();
			char[] buffer = new char[1024];
			Reader reader;
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			String jsonString = writer.toString();
			return jsonString;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				if(is != null){
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void saveDiskObject(Context context, String fileName, Object object) {
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

	public static String getNetData(String jsonData) {
		Log.d("getNetData", "jsonData="+jsonData);
		HttpURLConnection httpurlconnection = null;
		InputStream in = null;
		ByteArrayOutputStream swapStream = null;
		try {
			byte[] asedata = jsonData.getBytes("utf-8");
			URL url = new URL(SearchUrl);
			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setConnectTimeout(15000);
			httpurlconnection.setReadTimeout(15000);
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setDoInput(true);
			httpurlconnection.setUseCaches(false);

			httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			httpurlconnection.setRequestProperty("Accept-Encoding", "gzip");

			httpurlconnection.setRequestProperty("User-Agent", "Mozilla/4.0");
			httpurlconnection.setRequestProperty("Content-Length", Integer.toString(asedata.length));
			httpurlconnection.setRequestMethod("POST");

			DataOutputStream out = new DataOutputStream(httpurlconnection.getOutputStream());
			out.write(asedata);
			out.flush();
			out.close();

			in = httpurlconnection.getInputStream();
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = in.read(buff, 0, 1024)) > 0) {
				swapStream.write(buff, 0, len);
			}

			byte[] getserverdata = swapStream.toByteArray();
			String decompressData = new String(getserverdata);
			return decompressData;

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				swapStream.close();
				in.close();
				httpurlconnection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
