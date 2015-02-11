package com.cod3scr1b3r.wol;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Eyal on 03-1-15.
 */
public class AppDataStore {
    protected final static String PREF_NAME = "main_prefs";

    protected final static String KEY_DEFUALT_MAC = "default_mac";
    protected final static String KEY_DEFUALT_NET_ADDR = "default_net_addr";
	protected final static String KEY_LAST_USED_MAC = "last_mac";
	protected final static String KEY_LAST_USED_NET_ADDR = "last_net_addr";

    protected SharedPreferences mPrefs;
    protected Context mAppContext;

    public AppDataStore(Context appContext){
        //be on the safe side.
        mAppContext = appContext.getApplicationContext();
        mPrefs = mAppContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getDefaultMac(){
        return mPrefs.getString(KEY_DEFUALT_MAC, "");
    }

    public InetAddress getDefaultNetAddress(){
        InetAddress result = null;
        try{
            String addrAsString = mPrefs.getString(KEY_DEFUALT_NET_ADDR, "");
            if( ! TextUtils.isEmpty(addrAsString) ){
               result = InetAddress.getByName(addrAsString);
            }
        }catch(UnknownHostException e){
            //ignore should never happend as we are the one to save it.
        }
        return result;
    }

	public String getLastUsedMac(){
		return mPrefs.getString(KEY_LAST_USED_MAC, "");
	}

	public InetAddress getLastUsedNetAddress(){
		InetAddress result = null;
		try{
			String addrAsString = mPrefs.getString(KEY_LAST_USED_NET_ADDR, "");
			if( ! TextUtils.isEmpty(addrAsString) ){
				result = InetAddress.getByName(addrAsString);
			}
		}catch(UnknownHostException e){
			//ignore should never happend as we are the one to save it.
		}
		return result;
	}

    public void setDefaultMac(String mac){
        if( TextUtils.isEmpty(mac) ){
            throw new IllegalArgumentException("Cannot persist null MAC");
        }
        mPrefs.edit().putString(KEY_DEFUALT_MAC, mac).commit();
    }

    public void setDefaultNetAddress(InetAddress netAddress){
        if( netAddress == null ){
            throw new IllegalArgumentException("Cannot persist null net address");
        }
        mPrefs.edit().putString(KEY_DEFUALT_NET_ADDR, netAddress.getHostAddress()).commit();
    }

	public void setLastUsedMac(String mac){
		if( TextUtils.isEmpty(mac) ){
			throw new IllegalArgumentException("Cannot persist null MAC");
		}
		mPrefs.edit().putString(KEY_LAST_USED_MAC, mac).commit();
	}

	public void setLastUsedNetAddress(InetAddress netAddress){
		if( netAddress == null ){
			throw new IllegalArgumentException("Cannot persist null net address");
		}
		mPrefs.edit().putString(KEY_LAST_USED_NET_ADDR, netAddress.getHostAddress()).commit();
	}

}
