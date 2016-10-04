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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private TextView tvVersion;
	private String mVersionName;
	
	
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
	
	private void checkVersion() {
		
		// 启动子线程异步加载
		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://127.0.0.1:8090/safe");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					
					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inputStream = conn.getInputStream();
						String result = StreamUtils.readFromStream(inputStream);
						System.out.println("Json Result:" + result);
						
						JSONObject jo = new JSONObject(result);
						String mVersionName = jo.getString("versionName");
					}
					
				} catch (MalformedURLException e) {
					// URL错误
					e.printStackTrace();
				} catch (IOException e) {
					// 网络错误
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
			
	}

}
