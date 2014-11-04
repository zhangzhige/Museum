package com.example.museum;

import android.util.Log;

public class XL_Log {

	String TAG;
	
	public XL_Log(Class<?> class1) {
		TAG = class1.getSimpleName();
	}

	public void debug(String string) {
		Log.d(TAG, string);
	}

}
