package com.wugq.mobilesafe.activity;
import com.wugq.mobilesafe.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;


public class CameraActivity extends Activity {

	private SurfaceView cameraPreview;
	private Camera camera=null;
	private Callback cameraPreviewHolderCallback=new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			stopPreview();
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			startPreview();
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void startPreview(){
		camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		try {
			camera.setPreviewDisplay(cameraPreview.getHolder());			
			camera.setDisplayOrientation(90);
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stopPreview(){
		camera.stopPreview();
		camera.release();
	}
	
	private String saveImageTempFile(byte[] bytes) {
		try {
			File f = File.createTempFile("img", "");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bytes);
			fos.flush();
			fos.close();
			
			return f.getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
		cameraPreview.getHolder().addCallback(cameraPreviewHolderCallback);
		
		findViewById(R.id.btnTakePic).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				camera.takePicture(null, null, new Camera.PictureCallback() {
					
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						
						String imgPath = saveImageTempFile(data);
						System.out.println("ImagePath : " + imgPath);
						if (imgPath != null) {
							Intent i = new Intent(CameraActivity.this, ImagePreviewActivity.class);
							i.putExtra("path", imgPath);
							startActivity(i);
							
						}else {
							Toast.makeText(CameraActivity.this, "保存照片失败", Toast.LENGTH_SHORT).show();
						}
					}
				});

			}
		});
	}
	
}
