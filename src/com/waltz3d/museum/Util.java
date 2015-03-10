package com.waltz3d.museum;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class Util {

	public static boolean net_type_changed = false;

	public static void showToast(Context mContext, String string, int duration) {
		Toast.makeText(mContext, string, duration).show();
	}

	public static String getSDCardDir(Context mContext) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? Environment.getExternalStorageDirectory().getPath() : mContext.getCacheDir().getPath();
		String path = cachePath + File.separator;
		if (BuildConfig.DEBUG) {
			Log.d("getSDCardDir", "getSDCardDir=" + path);
		}
		return path;
	}

	public static boolean ensureDir(String path) {
		if (null == path) {
			return false;
		}
		boolean ret = false;
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			try {
				ret = file.mkdirs();
			} catch (SecurityException se) {
				se.printStackTrace();
			}
		}

		return ret;
	}

	public static boolean showDialog(ProgressDialog dialog, String msg) {
		if (dialog == null) {
			return false;
		}
		dismissDialog(dialog);

		dialog.setMessage(msg);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public static void dismissDialog(ProgressDialog dialog) {
		if (null != dialog) {
			try {
				dialog.dismiss();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			// dialog = null;
		}
	}

	public static boolean isWifiNet(Context context) {
		boolean bRet = false;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiInfo.isConnectedOrConnecting()) {
			bRet = true;
		}
		return bRet;
	}
}
