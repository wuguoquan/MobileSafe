package com.wugq.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

// 获取经纬度坐标
public class LocationService extends Service {

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
			
	}
}
