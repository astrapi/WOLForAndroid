package com.cod3scr1b3r.wol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eyal on 21-6-15.
 */
public class MacScanner {

    private static final String ARP_TABLE_POS = "/proc/net/arp";
    private static final String ARP_TABLE_REGEXP = "^([^ ]+) +[^ ]+ +[^ ]+ +([^ ]+) .*";
    private static final Pattern ARP_TABLE_PATTERN = Pattern.compile(ARP_TABLE_REGEXP);
    private static final int ONE_BYTE_MAX = 255;
    private static final int TWO_BYTE_MAX = 65535;
    private static final int THREE_BYTE_MAX = 16777215;
    private Map<String, String> mMacaddresses;
    private InetAddress mNetworkAddress;
    private InetAddress mSubnetMask;

    public MacScanner(InetAddress netAddress, InetAddress subnetMask){
        mNetworkAddress = netAddress;
        mSubnetMask = subnetMask;
    }

    private int getMaxHosts(byte[] subnetMask){
        int result = 0;
        result |= ~subnetMask[3] & 0x000000FF;
        result |= (~subnetMask[2] << 8) & 0x0000FF00;
        result |= (~subnetMask[1] << 16) & 0x00FF0000;
        result |= (~subnetMask[0] << 24) & 0xFF000000;
        return result;
    }

    private Map<String, String> getArpTable(String arpFileStr){
        Map<String, String> result = null;
        File arpFile = new File(arpFileStr);
        if( ! arpFile.exists() ){
            throw new IllegalArgumentException("arp file not found: " + arpFileStr);
        }
        if( arpFile.canRead() ){
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(arpFile));
                if( reader.readLine() != null ) {//skip first line
                    String line = reader.readLine();
                    result = new HashMap<>();
                    while (line != null) {
                        parseIntoResult(line, result);
                        line = reader.readLine();
                    }
                    if( result.size() == 0 ){
                        result = null;
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    private void parseIntoResult(String line, Map<String, String> result) {
        Matcher m = ARP_TABLE_PATTERN.matcher(line);
        if( m.matches() && m.groupCount() == 2){
            result.put(m.group(1), m.group(2));
        }
    }

    private void sendUDPPacket(InetAddress address){
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            DatagramPacket sendPacket = new DatagramPacket(new byte[]{}, 0, address, 8);
            clientSocket.setSoTimeout(5);
            clientSocket.send(sendPacket);
        }catch(IOException e){ /*ignore*/ };
    }


    /**
     * this method may block for quite a while, a lot of IO operation is going on here.
     * So it needs to be run on a side thread, asyncTask, service or other methods.
     */
    public void scanNetworkforMac(){
        int maxHostsTotal = getMaxHosts(mSubnetMask.getAddress());
        byte[] addrBytes = mNetworkAddress.getAddress();
        try {
            for (int i = 0; i < maxHostsTotal; i++) {
                byte[] temp = Arrays.copyOf(addrBytes, 4);
                temp[3] |= (byte) i;
                if( i > ONE_BYTE_MAX ){
                    temp[2] |= (byte) i & 0x0000FF00;
                }
                if( i > TWO_BYTE_MAX ){
                    temp[1] |= (byte) i & 0x00FF0000;
                }
                if( i > THREE_BYTE_MAX ){
                    temp[0] |= (byte) i & 0xFF000000;
                }
                InetAddress address = Inet4Address.getByAddress(temp);
                sendUDPPacket(address);
            }
        }catch(Exception e){ /* ignore */ }
        mMacaddresses = getArpTable(ARP_TABLE_POS);
    }

    public Map<String, String> getAddressesInfo(){
        return mMacaddresses;
    }

}
