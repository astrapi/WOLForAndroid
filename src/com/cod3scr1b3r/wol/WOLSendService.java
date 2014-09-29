package com.cod3scr1b3r.wol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public final class WOLSendService extends IntentService {

	public WOLSendService() {
		super("WOLService");
	}

	public static final String ACTION_WAKE = "com.cod3scr1b3r.wol.action.WAKEUP";
	public static final String PARAM_MAC_ADDR = "mac_address";
	
	private static final String TAG = "WOLSendService";
	
	public static void DoWakeUp(Context context, String macAddress){
		Intent intent = new Intent(ACTION_WAKE);
		intent.setClass(context, WOLActivity.class);
		if( ! TextUtils.isEmpty(macAddress) ){
			intent.putExtra(PARAM_MAC_ADDR, macAddress);
			context.startService(intent);
		}else{
			Log.e(TAG, "mac address was empty, cannot do WOL");
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		if( intent.getAction().equals(ACTION_WAKE) ){
			if( intent.hasExtra(PARAM_MAC_ADDR)){
				String macAddress = intent.getStringExtra(PARAM_MAC_ADDR);
				WakeOnLanGenerator.WakeNow(getApplicationContext(), macAddress);
			}else{
				android.util.Log.e(TAG, "got bad mac address, param was empty");
			}
		}//else if
			
	}
}
