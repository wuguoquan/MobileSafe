package com.wugq.mobilesafe.activity;

import com.wugq.mobilesafe.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
}
