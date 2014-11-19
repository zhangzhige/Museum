package com.example.museum;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * @author pengyiming
 * @date 2013-9-30
 * @function 自定义控件
 */
public class CoverFlow extends Gallery {

	private int mMaxRotationAngle = 60;
	private int mMaxZoom = -120;
	private int mCoveflowCenter;
	
	public boolean isNeedRotate = true ;

	public CoverFlow(Context context) {
		super(context);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setStaticTransformationsEnabled(true);
	}

	public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setStaticTransformationsEnabled(true);
	}

	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public int getMaxZoom() {
		return mMaxZoom;
	}

	public void setMaxZoom(int maxZoom) {
		mMaxZoom = maxZoom;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		Log.d(VIEW_LOG_TAG, "getCenterOfView left = "+ view.getLeft()+",width ="+view.getWidth());
		return view.getLeft() + view.getWidth() / 2;
	}

	
	protected boolean getChildStaticTransformation(View child, Transformation t) {

		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {
			transformImageBitmap(child, t, 0);
		} else {
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			Log.d(VIEW_LOG_TAG, "rotationAngl="+rotationAngle);
			transformImageBitmap(child, t, rotationAngle);
		}

		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(View child, Transformation t, int rotationAngle) {
		if(!isNeedRotate){
			return;
		}
		final int rotation = Math.abs(rotationAngle);
		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle) {
			child.setTranslationX(Math.abs(rotation*3));
		}
		child.setPivotX(child.getWidth() / 2);
		child.setPivotY(child.getHeight() / 2);
		child.setRotationY(rotationAngle);
	}
}