package com.cod3scr1b3r.wol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public final class WOLSendService extends IntentService {

	public static final String ACTION = "com.cod3scr1b3r.wol.action.WAKEUP";
	public static final String PARAM_MAC_ADDR = "mac_address";
	
	private static final String TAG = "WOLSendService";
	
	public static void DoWakeUp(Context context, String macAddress){
		Intent intent = new Intent(ACTION);
		intent.setClass(context, WOLActivity.class);
		if( ! TextUtils.isEmpty(macAddress) ){
			intent.putExtra(PARAM_MAC_ADDR, macAddress);
			context.startService(intent);
		}else{
			Log.e(TAG, "mac address was empty, cannot do WOL");
		}
	}
	
	public WOLSendService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}

}
