package com.example.museum;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Util {

	public static boolean net_type_changed = false;

	public static void showToast(Context mContext, String string, int duration) {
		Toast.makeText(mContext, string, duration).show();
	}

	public static String getSDCardDir(Context mContext) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? Environment
				.getExternalStorageDirectory().getPath() : mContext
				.getCacheDir().getPath();
		String path = cachePath + File.separator;
		if (BuildConfig.DEBUG) {
			Log.d("getSDCardDir", "getSDCardDir=" + path);
		}
		return path;
	}

}
