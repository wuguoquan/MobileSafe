package com.wugq.mobilesafe.activity;

import java.util.List;

import com.wugq.mobilesafe.R;
import com.wugq.mobilesafe.service.LocationService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class RmSafeActivity extends Activity {

	private CheckBox cbSimStatus;
	private TextView tvSimDesc;
	private CheckBox cbContactStatus;
	private TextView tvContactDesc;
	private CheckBox cbGpsStatus;
	private TextView tvGpsDesc;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rmsafe);
		
		// 创建配置信息到本地
		final SharedPreferences mPref = getSharedPreferences("config", MODE_PRIVATE);
		// 写配置信息到本地
		mPref.edit().putString("sim", "13510573720").commit();
		// 读配置信息从本地
		System.out.println("SIM : " + mPref.getString("sim", null));
		
		cbSimStatus = (CheckBox) findViewById(R.id.cb_sim_status);
		tvSimDesc = (TextView) findViewById(R.id.tv_sim_desc);
		cbContactStatus = (CheckBox) findViewById(R.id.cb_contact_status);
		tvContactDesc = (TextView) findViewById(R.id.tv_contact_desc);
		cbGpsStatus = (CheckBox) findViewById(R.id.cb_gps_status);
		tvGpsDesc = (TextView) findViewById(R.id.tv_gps_desc);

		
				
		if (mPref.getString("IsUseSim", "false").equals("true")) {
			tvSimDesc.setText("SIM卡已绑定" + "(" + mPref.getString("simSN", null) + ")");
			cbSimStatus.setChecked(true);
		}else {
			tvSimDesc.setText("SIM卡未绑定");
			cbSimStatus.setChecked(false);
		}
		
		if (mPref.getString("IsUseContact", "false").equals("true")) {
			tvContactDesc.setText("联系人已绑定" + "(" + mPref.getString("contactName", null) +
					":" + mPref.getString("contactPhone", null) +")");
			cbContactStatus.setChecked(true);
		}else {
			tvContactDesc.setText("联系人未绑定");
			cbContactStatus.setChecked(false);
		}
		
		if (mPref.getString("IsUseGps", "false").equals("true")) {
			tvGpsDesc.setText("GPS已绑定");
			cbGpsStatus.setChecked(true);			
		}else {
			tvGpsDesc.setText("GPS未绑定");
			cbGpsStatus.setChecked(false);	
		}
		
					
		cbSimStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 判断当前勾选状态
				if (cbSimStatus.isChecked()) {				
					// 写配置信息到本地
					mPref.edit().putString("IsUseSim", "true").commit();					
					// 获取sim卡系统服务
					TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					// 获取sim卡序列号
					String simSN = tm.getSimSerialNumber();
					// 保存sim卡序列号
					mPref.edit().putString("simSN", simSN).commit();
					// 显示sim卡序列号
					tvSimDesc.setText("SIM卡已绑定" + "(" + mPref.getString("simSN", null) + ")");
					
				}else {
					tvSimDesc.setText("SIM卡未绑定");
					// 写配置信息到本地
					mPref.edit().putString("IsUseSim", "false").commit();
					// 撤销sim卡序列号
					mPref.edit().remove("simSN").commit();
				}
			}
		});
		
		
		cbContactStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 判断当前勾选状态
				if (cbContactStatus.isChecked()) {				
					// 写配置信息到本地
					mPref.edit().putString("IsUseContact", "true").commit();					
					// 保存联系人信息到本地
					readContact(mPref);
					// 显示联系人信息
					tvContactDesc.setText("联系人已绑定" + "(" + mPref.getString("contactName", null) +
							":" + mPref.getString("contactPhone", null) +")");
					// 发送短信给联系人
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(mPref.getString("contactPhone", null), null, 
							"已绑定联系人", null, null);
					
					// 播放报警音乐
				    MediaPlayer player = MediaPlayer.create(RmSafeActivity.this, R.raw.ylzs);
				    player.setVolume(1f, 1f);
				    player.setLooping(true);
				    player.start();
				    
					
				}else {
					tvContactDesc.setText("联系人未绑定");
					// 写配置信息到本地
					mPref.edit().putString("IsUseContact", "false").commit();
					// 撤销联系人信息
					mPref.edit().remove("contactName").commit();
					mPref.edit().remove("contactPhone").commit();
				}				
			}
		});  
		
		cbGpsStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 判断当前勾选状态
				if (cbGpsStatus.isChecked()) {				
					// 写配置信息到本地
					mPref.edit().putString("IsUseGps", "true").commit();
					// 获取系统定位服务
//					LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//					List<String> allProviders = lm.getAllProviders();
//					System.out.println(allProviders);
//					
//					MyLocationListener listener = new MyLocationListener();
//					lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
					
					// 获取自定义定位服务
					startService(new Intent(RmSafeActivity.this, LocationService.class));
					
					//tvGpsDesc.setText("GPS已绑定");
					
				}else {
					tvGpsDesc.setText("GPS未绑定");
					// 写配置信息到本地
					mPref.edit().putString("IsUseGps", "false").commit();
				}
			}
		});		
		
		
	}
	
	
	private void readContact(SharedPreferences mPref) {
		// 首先，从raw_contacts中读取联系人的id（"contact_id"）
		// 其次，根据contact_id从data表中查询出相应得电话号码和联系人名称
		// 最后，然后根据mimetype来区分哪个是联系人名称，哪个是电话号码
		
		Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor rawContactCursor = getContentResolver().query(rawContactsUri, 
				new String[]{"contact_id"}, null, null, null);
		// 遍历
		if (rawContactCursor != null) {
			while (rawContactCursor.moveToNext()) {
				String contactId = rawContactCursor.getString(0);
				System.out.println("contactId : " + contactId);
				
				Cursor dataCursor = getContentResolver().query(dataUri, 
						new String[]{"data1", "mimetype"}, "contact_id=?", 
						new String[]{contactId}, null);
				// 遍历
				if (dataCursor != null) {
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);
						System.out.println("data1 : " + data1 + ";" + "mimetype : " + mimetype);
						
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							// 保存联系人电话号码
							mPref.edit().putString("contactPhone", data1.replaceAll(" ", "")).commit();
						}else if ("vnd.android.cursor.item/name".equals(mimetype)) {
							// 保存联系人姓名
							mPref.edit().putString("contactName", data1).commit();
						}
					}
					dataCursor.close();
				}				
			}
			rawContactCursor.close();
		}				
	}
	
	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			String j = "经度：" + location.getLongitude();
			String w = "纬度：" + location.getLatitude();
			String accuracy = "精确度：" + location.getAccuracy();
			String altitude = "海拔：" + location.getAltitude();
			
			System.out.println("GPS : onLocationChanged");
			//tvGpsDesc.setText(j + w + accuracy + altitude);
			
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			System.out.println("GPS : onStatusChanged");
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			System.out.println("GPS : onProviderEnabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			System.out.println("GPS : onProviderDisabled");
		}				
	}
	
	
}
