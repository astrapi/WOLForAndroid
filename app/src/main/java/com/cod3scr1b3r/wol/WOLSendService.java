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

	private static final String TAG = "WOLSendService";

    /**
     * wrapper method that calls the service for you using the given params
     * @param context
     * @param macAddress
     * @param netIpAddr
     */
	public static void DoWakeUp(Context context, String[] macAddress, InetAddress netIpAddr){
		Intent intent = new Intent(ACTION_WAKE);
		intent.setClass(context, WOLSendService.class);
		intent.putExtra(PARAM_MAC_ADDR, macAddress);
        intent.putExtra(PARAM_NET_ADDR, netIpAddr);
    	context.startService(intent);
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
        String[] macAddresses = intent.getStringArrayExtra(PARAM_MAC_ADDR);
        AppDataStore dataStore = ((WOLApp)getApplication()).getDataStrore();
        String defaultMac = dataStore.getDefaultMac();
        if( macAddresses == null && ! TextUtils.isEmpty(defaultMac) ){
            macAddresses = new String[] { defaultMac };
            Object temp = intent.getSerializableExtra(PARAM_NET_ADDR);
            InetAddress netIpAddr = null;
            InetAddress defaultNetAddress = dataStore.getDefaultNetAddress();
            if( temp == null && defaultNetAddress != null){
                netIpAddr = defaultNetAddress;
            }else if( temp != null && temp instanceof InetAddress){
                netIpAddr = (InetAddress)temp;
            }
            if(  netIpAddr != null ) {
                int len = macAddresses.length;
                for(int i = 0; i < len; i++) {
                    try {
                        WakeOnLanGenerator.WakeNow(this, macAddresses[i], netIpAddr);
                    } catch (Exception e) {
                        Log.e(TAG, "faled to wake device! ");
                        e.printStackTrace();
                    }
                }
            }else{
                Log.e(TAG, "could not get default net address and bad net address given as param");
            }
        }else{
            Log.e(TAG, "no mac was given and no default mac exists.");
        }
    }
}
