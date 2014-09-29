package com.cod3scr1b3r.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.cod3scr1b3r.wol.expcetions.MACFormatException;

public class WakeOnLanGenerator {
	public static final int DEFAULT_PORT = 9;    
	public static final String TAG = "WOL_GEN";
	
	private static final String MAC_ADDRESS_REG_PATTERN = "^[0-9a-fA-F]{2}((:|\\-)[0-9a-fA-F]{2}){5}$";
	
    public static void WakeNow(Context context, String macAddress) {
    	if( isConnectedToWiFI(context) && ! TextUtils.isEmpty(macAddress) ){
	        
	        try {
	        	//see : http://en.wikipedia.org/wiki/Wake-on-LAN#Magic_packet
	            byte[] macBytes = macStringToBytes(macAddress);
	            byte[] bytes = new byte[6 + 16 * macBytes.length];
	            for (int i = 0; i < 6; i++) {
	                bytes[i] = (byte) 0xff;
	            }
	            //String from byte #6 write the mac address to the buffer and increase the counter by it's size, untill the end
	            //of the assign bugger, should be 16 iterations.
	            for (int i = 6; i < bytes.length; i += macBytes.length) {
	                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
	            }
	            int bcAddress = getBroadcastAddressDHCP(context);
	            if( bcAddress > 0 ){
	            	Log.i(TAG, "Got broadcast address, set to be: " + bcAddress);
		            InetAddress address = intToINetAddress(bcAddress);
		            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, DEFAULT_PORT);
		            DatagramSocket socket = new DatagramSocket();
		            socket.send(packet);
		            socket.close();
	            }else{
	            	Log.e(TAG, "Failed to get broadcast address");
	            }
	        }catch (Exception e) {
	            Log.e(TAG, "Failed to send Wake-on-LAN packet:" + e.getMessage());
	            e.printStackTrace();
	        }
    	}
        
    }
    
    public static InetAddress intToINetAddress(int hostAddress) {
		byte[] addressBytes = { (byte)(0xff & hostAddress),
								(byte)(0xff & (hostAddress >> 8)),
								(byte)(0xff & (hostAddress >> 16)),
								(byte)(0xff & (hostAddress >> 24)) //last item is the  
		};
		
		try{
			return InetAddress.getByAddress(addressBytes);
		}catch (UnknownHostException e) {
			android.util.Log.e(TAG, "error while trying to get address from int", e);
			throw new AssertionError(e.getMessage());
		}
	}
    
    /**
     * get broadcast address based on last DHCP.
     * TODO change it to support static ip as well.
     * @param context
     * @return
     */
    public static int getBroadcastAddressDHCP(Context context){
    	int result = -1;
    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	DhcpInfo dhcp = wifiManager.getDhcpInfo();
    	if( dhcp.netmask != 0 ){
    		result = ~dhcp.netmask | dhcp.ipAddress;
    	}
    	return result;
    }
    
    public static boolean isConnectedToWiFI(Context context){
    	ConnectivityManager connManager = (ConnectivityManager)
    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }
    
    public static boolean validateMacString(String mac){
    	boolean result = ! TextUtils.isEmpty(mac) && mac.matches(MAC_ADDRESS_REG_PATTERN);
    	return result;
    }
    
    /**
     * splits the macaddress string from the user into bytes.
     * mac address format: aa:bb:cc:ee:ff:99 (6 bytes).
     * @param macAddressStr
     * @return
     * @throws IllegalArgumentException
     */
    private static byte[] macStringToBytes(String macAddressStr) throws MACFormatException {
        byte[] bytes = new byte[6];
        //split mac
        String[] macBytesArr = macAddressStr.split("(:|\\-)");
        if (macBytesArr.length != 6) {
            throw new MACFormatException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(macBytesArr[i], 16);
            }
        }catch (NumberFormatException e) {
            throw new MACFormatException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

}
