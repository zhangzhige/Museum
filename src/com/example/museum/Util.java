package com.example.museum;

import android.content.Context;
import android.widget.Toast;

public class Util {

	public static boolean net_type_changed = false ;

	public static void showToast(Context mContext, String string, int duration) {
		Toast.makeText(mContext, string, duration).show();
	}

}
