package com.example.museum;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.gcacace.signaturepad.utils.SignaturePad;

public class SignActivity extends Activity {

	private SignaturePad mSignaturePad;
	
	private Button mClearButton;
	
	private Button mSaveButton;
	
	public static Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);

		mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
		mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
			@Override
			public void onSigned() {
				mSaveButton.setEnabled(true);
				mClearButton.setEnabled(true);
			}

			@Override
			public void onClear() {
				mSaveButton.setEnabled(false);
				mClearButton.setEnabled(false);
			}
		});

		mClearButton = (Button) findViewById(R.id.clear_button);
		mSaveButton = (Button) findViewById(R.id.save_button);

		mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSignaturePad.clear();
			}
		});

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mBitmap = mSignaturePad.getSignatureBitmap();
				finish();
			}
		});
	}
}
