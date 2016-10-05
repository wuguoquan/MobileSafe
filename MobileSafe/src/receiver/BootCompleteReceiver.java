package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

// 监听手机开机启动广播
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		String simSN = sp.getString("simSN", null);
		
		if (!simSN.isEmpty()) {
			// 获取当前手机的sim卡序列号
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELECOM_SERVICE);
			String currentsimSN = tm.getSimSerialNumber();
			
			if (simSN.equals(currentsimSN)) {
				System.out.println("手机安全");
			}else {
				System.out.println("手机不安全，发送短信");
			}
		}

	
	}

}
