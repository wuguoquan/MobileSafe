package com.wugq.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

// 短信拦截
public class SmsReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		
		for (Object object : objects) {
			SmsMessage message = SmsMessage.createFromPdu((byte[])object);
			// 获取拦截短信的来源地址
			String originatingAddress = message.getOriginatingAddress();
			// 获取拦截短信的内容
			String messageBody = message.getMessageBody();
			
			System.out.println("短信拦截" + originatingAddress + ":" + messageBody);
		}
	}

}
