package com.wugq.mobilesafe.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.wugq.mobilesafe.R;
import com.wugq.mobilesafe.utils.StreamUtils;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTER_HOME = 4;
	
	
	private TextView tvVersion;
	private String mVersionName;
	private int mVersionCode;
	private String mDesc;
	private String mDownUrl;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDailog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "NET错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "JSON错误", Toast.LENGTH_SHORT).show();
				enterHome();
				break;	
			case CODE_ENTER_HOME:
				
				enterHome();
				break;	
			default:
				break;
			}			
		}	
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash); 
		
		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本号: " + getVersionName());
		
		checkVersion();
	}
	
	private String getVersionName() {
		
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);  // 获取包的信息
			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;		
			System.out.println("versionCode = " + versionCode + " ; " +"versionName = " + versionName);		
			return versionName;			
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private int getVersionCode() {
		
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);  // 获取包的信息
			int versionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;		
			System.out.println("versionCode = " + versionCode + " ; " +"versionName = " + versionName);		
			return versionCode;			
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	private void checkVersion() {
		
		final long startTime = System.currentTimeMillis();
		// 启动子线程异步加载
		new Thread() {

			@Override
			public void run() {
				
				// 建立消息机制
				Message msg = Message.obtain();
				HttpURLConnection conn = null;
				
				try {
					URL url = new URL("http://127.0.0.1:8090/safe");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					
					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);
						System.out.println("Json Result:" + result);
						
						//解析JSON
						JSONObject jo = new JSONObject(result);
						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownUrl = jo.getString("downloadUrl");
						
						System.out.println("NetversionCode: " + mVersionCode);
						
						// 判断是否有更新
						if (mVersionCode > getVersionCode()) {
							
							msg.what = CODE_UPDATE_DIALOG;
							// 子线程更新UI，需要句柄和消息
							//showUpdateDailog();
						}else {
							msg.what = CODE_ENTER_HOME;
						}												
					}
					
				} catch (MalformedURLException e) {
					// URL错误
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// 网络错误
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// JSON解析失败
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					
					long endTime = System.currentTimeMillis();
					long timeUsed = endTime - startTime;
					// 强制休眠一段时间，保证闪屏展示1秒
					if (timeUsed < 1000) {
						try {
							Thread.sleep(1000 - timeUsed);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// 子线程结束前需要做
					mHandler.sendMessage(msg);
					// 关闭网络连接
					if (conn != null) {
						conn.disconnect(); 
					}
				}
			}
		}.start();
			
	}

	protected void showUpdateDailog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本 : " + mVersionName);
		builder.setMessage(mDesc);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.out.println("立即更新");
			}
		});
		
		builder.setNegativeButton("以后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.out.println("以后再说");
				enterHome();
			}
		});
		
		builder.show();
	}
	
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish(); // 当跳到Home界面，把Splash界面取消
	}
	

}
