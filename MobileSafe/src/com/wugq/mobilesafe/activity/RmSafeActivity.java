package com.wugq.mobilesafe.activity;

import com.wugq.mobilesafe.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class RmSafeActivity extends Activity {

	private CheckBox cbSimStatus;
	private TextView tvSimDesc;
	
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
				
		if (mPref.getString("IsUseSim", "false").equals("true")) {
			tvSimDesc.setText("SIM卡已绑定" + "(" + mPref.getString("simSN", null) + ")");
			cbSimStatus.setChecked(true);
		}else {
			tvSimDesc.setText("SIM卡未绑定");
			cbSimStatus.setChecked(false);
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
		
	}
}
