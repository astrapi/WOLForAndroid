package com.cod3scr1b3r.wol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cod3scr1b3r.wol.expcetions.WOLException;


public class WakeOnLanGenerator {
	public static final int DEFAULT_PORT = 9;    
	public static final String TAG = "WOL_GEN";

    /**
     * create magic packet from the mac adderss and broadcast it to the broadcast address
     * @param context
     * @param macAddress
     * @param broadcastAddress
     * @throws com.cod3scr1b3r.wol.expcetions.WOLException if we failed to send teh packet
     * @throws java.lang.IllegalArgumentException if mac address or braodcast address is null
     * @return true on success send false on error(WIFI not connected)
     */
    public static boolean WakeNow(Context context, String macAddress, InetAddress broadcastAddress)throws WOLException{
        if( TextUtils.isEmpty(macAddress) || broadcastAddress == null ){
            throw new IllegalArgumentException("macAddress and broadcastAddress cannot be null");
        }
        boolean result = false;
    	if( NetworkUtils.isConnectedToWiFI(context) ){
	        try {
                byte[] magicPacketBytes = generateMagicPacketBytes(macAddress);
	            int bcAddress = NetworkUtils.INetAddressToint(broadcastAddress);
	            if( bcAddress != 0 ){
	            	Log.i(TAG, String.format("Got broadcast address, set to be: %x",bcAddress));
		            InetAddress address = NetworkUtils.intToINetAddress(bcAddress);
		            DatagramPacket packet = new DatagramPacket(magicPacketBytes, magicPacketBytes.length, address, DEFAULT_PORT);
		            DatagramSocket socket = new DatagramSocket();
		            socket.send(packet);
		            socket.close();
                    result = true;
	            }else{
	            	Log.e(TAG, "no broadcast address supplied");
                    throw new IllegalArgumentException("broadcast address cannot be null");
	            }
	        }catch (Exception e) {
	            Log.e(TAG, "Failed to send Wake-on-LAN packet:" + e.getMessage());
                throw new WOLException(e);
	        }
    	}else{
            Log.e(TAG, "WIFI is not connected");
        }
        return result;
        
    }

    /**
     * generate magic paket out of the given mac address
     * @param macAsString
     * @return
     */
    public static byte[] generateMagicPacketBytes(String macAsString){
        //see : http://en.wikipedia.org/wiki/Wake-on-LAN#Magic_packet
        byte[] macBytes = NetworkUtils.macStringToBytes(macAsString);
        byte[] bytes = new byte[6 + 16 * macBytes.length];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) 0xff;
        }
        //String from byte #6 write the mac address to the buffer and increase the counter by it's size, untill the end
        //of the assign buffer, should be 16 iterations.
        for (int i = 6; i < bytes.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
        }
        return bytes;
    }

}
