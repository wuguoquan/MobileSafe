package com.wugq.mobilesafe.activity;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImagePreviewActivity extends Activity {

	private ImageView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		iv = new ImageView(this);
		
		setContentView(iv);
		
		String path = getIntent().getStringExtra("path");
		if (path != null) {
			iv.setImageURI(Uri.fromFile(new File(path)));
		}
	}
}
