package com.cod3scr1b3r.wol;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.cod3scr1b3r.wol.expcetions.MACFormatException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Created by Eyal on 09-12-14.
 */
public class NetworkUtils {

    private static final String MAC_ADDRESS_REG_PATTERN = "^[0-9a-fA-F]{2}((:|\\-)[0-9a-fA-F]{2}){5}$";
    private static final String IP4_REG_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
    private static  final String TAG = "NET_UTILS";

    /**
     * splits the macaddress string from the user into bytes.
     * mac address format: aa:bb:cc:ee:ff:99 (6 bytes).
     * @param macAddressStr
     * @return
     * @throws IllegalArgumentException
     */
    /*package*/ static byte[] macStringToBytes(String macAddressStr) throws MACFormatException {
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

    /**
     * converts a string p4 address to InetAddress class.
     * @param addr
     * @return
     */
    public static InetAddress ip4String2Inet(String addr){
        if( TextUtils.isEmpty(addr) || ! Pattern.matches(IP4_REG_PATTERN, addr) ){
            throw new IllegalArgumentException("addr is null or not ipv4 address");
        }else {
            InetAddress address = null;
            try {
                address = InetAddress.getByName(addr);
            } catch (UnknownHostException e) {
                throw new IllegalArgumentException(e);
            }
            return address;
        }
    }

    /**
     * checks if the android client is currently connected to WIFI (could also be connected to GPRS,
     * BT network and the like.
     * @param context
     * @return true if network is connected to wifi.
     */
    public static boolean isConnectedToWiFI(Context context){
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    /**
     * validate mac address from the format aa:bb:cc:dd:ee:ff (6 bytes, in hex repr.
     * separated by ':'
     * @param mac
     * @return true if valid mac address sstring.
     */
    public static boolean validateMacString(String mac){
        boolean result = ! TextUtils.isEmpty(mac) && mac.matches(MAC_ADDRESS_REG_PATTERN);
        return result;
    }

    /**
     * get broadcast address based on last DHCP.
     * TODO change it to support static ip as well.
     * @param context
     * @return
     */
    public static int getBroadcastAddressDHCP(Context context){
        int result = 0;
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if( dhcp.netmask != 0 ){
            result = ~dhcp.netmask | dhcp.ipAddress;
        }
        return result;
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

    public static int INetAddressToint(InetAddress addr){
        if( addr != null ){
            byte[] bytes = addr.getAddress();
            int result = (bytes[0] & 0xff) | ((bytes[1] & 0xff)  << 8) | ((bytes[2] & 0xff)  << 16) |
                    ((bytes[3] & 0xff)  << 24);
            return result;
        }else{
            throw new IllegalArgumentException("INetAddress cannot be null!");
        }
    }


}
