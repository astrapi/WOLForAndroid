package com.cod3scr1b3r.wol;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.net.InetAddress;

public final class WOLSendService extends IntentService {

	public WOLSendService() {
		super("WOLService");
	}

	public static final String ACTION_WAKE = "com.cod3scr1b3r.wol.action.WAKEUP";
	public static final String PARAM_MAC_ADDR = "mac_address";
    public static final String PARAM_NET_ADDR = "net_address";
    public static final String PARAM_SUBNET_MUSK = "subnet_musk";
	
	private static final String TAG = "WOLSendService";

    /**
     * wrapper method that calls the service for you using the given params
     * @param context
     * @param macAddress
     * @param netIpAddr
     */
	public static void DoWakeUp(Context context, String macAddress, InetAddress netIpAddr){
		Intent intent = new Intent(ACTION_WAKE);
		intent.setClass(context, WOLSendService.class);
		if( ! TextUtils.isEmpty(macAddress) ){
			intent.putExtra(PARAM_MAC_ADDR, macAddress);
            intent.putExtra(PARAM_NET_ADDR, netIpAddr);
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
		    onWOLRequest(intent);
		}
			
	}

    private void onWOLRequest(Intent intent) {
        String macAddress = intent.getStringExtra(PARAM_MAC_ADDR);
        if( intent.getSerializableExtra(PARAM_NET_ADDR) instanceof InetAddress ) {
            InetAddress netIpAddr = (InetAddress) intent.getSerializableExtra(PARAM_NET_ADDR);
            if (TextUtils.isEmpty(macAddress) || netIpAddr != null) {
                Log.e(TAG, "one of the params was empty, dropping request");
            } else {
                try {
                    WakeOnLanGenerator.WakeNow(this, macAddress, netIpAddr);
                }catch(Exception e){
                    Log.e(TAG, "faled to wake device! ");
                    e.printStackTrace();
                }
            }
        }else{
            Log.e(TAG, PARAM_NET_ADDR + "param is NOT InetAddress instance! dropping request");
        }

    }
}
