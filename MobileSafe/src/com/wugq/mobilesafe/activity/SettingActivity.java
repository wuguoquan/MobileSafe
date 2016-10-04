package com.wugq.mobilesafe.activity;

import com.wugq.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingActivity extends Activity {

	private CheckBox cbStatus;
	private TextView tvDesc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_setting);
		
		cbStatus = (CheckBox) findViewById(R.id.cb_status);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		
		cbStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 判断当前勾选状态
				if (cbStatus.isChecked()) {
					
					tvDesc.setText("自动更新已经开启");
				}else {
					tvDesc.setText("自动更新已经关闭");
				}
			}
		});
		
	}
}
