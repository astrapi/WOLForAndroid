package com.cod3scr1b3r.wol;

import java.net.InetAddress;

/**
 * used to create a resolver that can get us one type of network broadcast address,
 * for example: DHCP WIFI, USB OTG ethernet adapter network, static WIFI, BT address etc...
 * BT is a bad example since no WOL will work with it since it's not ETHERNET based.
 * Created by Eyal on 09-12-14.
 */
public interface IBroadcastAddressProvider {
    public  InetAddress getBroadcastAddress();
}
