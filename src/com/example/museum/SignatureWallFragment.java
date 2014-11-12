package com.example.museum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SignatureWallFragment extends Fragment {

	private View mRootView;

	private ImageView imageView_sign_area;

	private FrameLayout sign_area_layout;

	private ImageView imageView_save_sign;

	private RelativeLayout layout_sign_save;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_sign, container, false);
		sign_area_layout = (FrameLayout) mRootView.findViewById(R.id.sign_area_layout);
		layout_sign_save = (RelativeLayout) mRootView.findViewById(R.id.layout_sign_save);
		imageView_sign_area = (ImageView) mRootView.findViewById(R.id.imageView_sign_area);
		sign_area_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), SignActivity.class));
			}
		});
		imageView_save_sign = (ImageView) mRootView.findViewById(R.id.imageView_save_sign);
		imageView_save_sign.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_sign_save.setDrawingCacheEnabled(true);
				Bitmap mBitmap = layout_sign_save.getDrawingCache();
				boolean isSuccess = addSignatureToGallery(mBitmap);
				if (isSuccess) {
					Toast.makeText(getActivity(), "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
					imageView_sign_area.setImageDrawable(new BitmapDrawable());
					SignActivity.mBitmap.recycle();
					SignActivity.mBitmap = null;
				} else {
					Toast.makeText(getActivity(), "Unable to store the signature", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SignActivity.mBitmap != null) {
			imageView_sign_area.setImageBitmap(SignActivity.mBitmap);
		}
	}

	public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0, 0, null);
		OutputStream stream = new FileOutputStream(photo);
		newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		stream.close();
	}

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
		if (!file.mkdirs()) {
			Log.e("SignaturePad", "Directory not created");
		}
		return file;
	}

	public boolean addSignatureToGallery(Bitmap signature) {
		boolean result = false;
		try {
			File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
			saveBitmapToJPG(signature, photo);
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri contentUri = Uri.fromFile(photo);
			mediaScanIntent.setData(contentUri);
			getActivity().sendBroadcast(mediaScanIntent);
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
