package com.waltz3d.museum;

import java.util.HashMap;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GifDecoder {
 
	private HashMap<Integer, Bitmap> mImageMap = new HashMap<Integer, Bitmap>();
	
	private int mCurrentIndex = 0;
	
	private int[] drawableResList = new int[]{R.drawable.ht_00000,
			R.drawable.ht_00001,
			R.drawable.ht_00002,
			R.drawable.ht_00003,
			R.drawable.ht_00004,
			R.drawable.ht_00005,
			R.drawable.ht_00006,
			R.drawable.ht_00007,
			R.drawable.ht_00008,
			R.drawable.ht_00009,
			R.drawable.ht_00010,
			R.drawable.ht_00011,
			R.drawable.ht_00012,
			R.drawable.ht_00013,
			R.drawable.ht_00014,
			R.drawable.ht_00015,
			R.drawable.ht_00016,
			R.drawable.ht_00017,
			R.drawable.ht_00018,
			R.drawable.ht_00019,
			R.drawable.ht_00020,
			R.drawable.ht_00021,
			R.drawable.ht_00022,
			R.drawable.ht_00023,
			R.drawable.ht_00024,
			R.drawable.ht_00025,
			R.drawable.ht_00026,
			R.drawable.ht_00027,
			R.drawable.ht_00028,
			R.drawable.ht_00029,
			R.drawable.ht_00030,
			R.drawable.ht_00031,
			R.drawable.ht_00032,
			R.drawable.ht_00033,
			R.drawable.ht_00034,
			R.drawable.ht_00035,
			R.drawable.ht_00036,
			R.drawable.ht_00037,
			R.drawable.ht_00038,
			R.drawable.ht_00039,
			R.drawable.ht_00040,
			R.drawable.ht_00041,
			R.drawable.ht_00042,
			R.drawable.ht_00043,
			R.drawable.ht_00044,
			R.drawable.ht_00045,
			R.drawable.ht_00046,
			R.drawable.ht_00047,
			R.drawable.ht_00048,
			R.drawable.ht_00049,
			R.drawable.ht_00050,
			R.drawable.ht_00051,
			R.drawable.ht_00052,
			R.drawable.ht_00053,
			R.drawable.ht_00054,
			R.drawable.ht_00055,
			R.drawable.ht_00056,
			R.drawable.ht_00057,
			R.drawable.ht_00058,
			R.drawable.ht_00059,
			R.drawable.ht_00060,
			R.drawable.ht_00061,
			R.drawable.ht_00062,
			R.drawable.ht_00063,
			R.drawable.ht_00064,
			R.drawable.ht_00065,
			R.drawable.ht_00066,
			R.drawable.ht_00067,
			R.drawable.ht_00068,
			R.drawable.ht_00069,
			R.drawable.ht_00070,
			R.drawable.ht_00071,
			R.drawable.ht_00072,
			R.drawable.ht_00073,
			R.drawable.ht_00074,
			R.drawable.ht_00075,
			R.drawable.ht_00076,
			R.drawable.ht_00077,
			R.drawable.ht_00078,
			R.drawable.ht_00079,
			R.drawable.ht_00080,
			R.drawable.ht_00081,
			R.drawable.ht_00082,
			R.drawable.ht_00083,
			R.drawable.ht_00084,
			R.drawable.ht_00085,
			R.drawable.ht_00086,
			R.drawable.ht_00087,
			R.drawable.ht_00088,
			R.drawable.ht_00089,
			R.drawable.ht_00090,
			R.drawable.ht_00091,
			R.drawable.ht_00092,
			R.drawable.ht_00093,
			R.drawable.ht_00094,
			R.drawable.ht_00095,
			R.drawable.ht_00096,
			R.drawable.ht_00097,
			R.drawable.ht_00098,
			R.drawable.ht_00099,
			R.drawable.ht_00100};
	
	public int width=200;

	public int height=200;

	public Bitmap getImage(Context mContext) {
		return next(mContext);
	}

	public Bitmap next(Context mContext) {
		int index = mCurrentIndex%drawableResList.length;
		Bitmap mBitmap = mImageMap.get(index);
		if(mBitmap != null){
			//TODO return;
		}else{
			mBitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableResList[index]);
			mImageMap.put(index, mBitmap);
		}
		mCurrentIndex+=1;
		return mBitmap;
	}

	public void onStop() {
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
