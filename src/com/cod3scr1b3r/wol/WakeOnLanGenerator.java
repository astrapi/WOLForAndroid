package com.cod3scr1b3r.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.text.TextUtils;
import android.util.Log;

import com.cod3scr1b3r.wol.expcetions.MACFormatException;

public class WakeOnLanGenerator {
	public static final int DEFAULT_PORT = 9;    
	public static final String TAG = "WOL_GEN";
	
	private static final String MAC_ADDRESS_REG_PATTERN = "^[0-9a-fA-F]{2}((:|\\-)[0-9a-fA-F]{2}){5}$";
	private static final String IP_ADDRESS_REG_PATTERN = "^(\\d\\.){3}\\d$";
	
    public static void WakeNow(String broadcastIP, String macAddress) {
    	if( ! TextUtils.isEmpty(broadcastIP) && ! TextUtils.isEmpty(macAddress) ){
	        
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
	            
	            InetAddress address = InetAddress.getByName(broadcastIP);
	            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, DEFAULT_PORT);
	            DatagramSocket socket = new DatagramSocket();
	            socket.send(packet);
	            socket.close();
	        }catch (Exception e) {
	            Log.e(TAG, "Failed to send Wake-on-LAN packet:" + e.getMessage());
	            e.printStackTrace();
	        }
    	}
        
    }
    
    public static boolean validateMacString(String mac){
    	boolean result = ! TextUtils.isEmpty(mac) && mac.matches(MAC_ADDRESS_REG_PATTERN);
    	return result;
    }
    
    public static boolean validateIPAddress(String ip){
    	boolean result = ! TextUtils.isEmpty(ip) && ip.matches(IP_ADDRESS_REG_PATTERN);
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
