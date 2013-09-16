/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import static org.junit.Assert.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.math.BigInteger;
import java.net.SocketException;

import org.junit.Test;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pedro F. Hernandez (Digital Rounin)
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is test code and have to hardcode values
public class AndroidNetInfoTest {

    protected static final Logger LOG = LoggerFactory.getLogger(AndroidNetInfoTest.class);

    private transient NetworkInterfaceInfo mNetworkInterfaceInfo;
    private transient WifiInfo mAndroidWifiInfo;
    private transient WifiManager mAndroidWifiManager;
    private transient Context mAndroidContext;
    private transient AndroidNetInfo mAndroidNetInfo;

    @Before
    public void before() throws SocketException {
        // NetworkInterfaceInfo
        mNetworkInterfaceInfo = mock(NetworkInterfaceInfo.class);

        // Prepare: Context->WifiManager->WifiInfo
        mAndroidWifiInfo = mock(WifiInfo.class);

        mAndroidWifiManager = mock(WifiManager.class);
        when(mAndroidWifiManager.getConnectionInfo()).thenReturn(mAndroidWifiInfo);

        mAndroidContext = mock(Context.class);
        when(mAndroidContext.getSystemService(Context.WIFI_SERVICE))
                .thenReturn(mAndroidWifiManager);

        // AndroidNetInfo
        mAndroidNetInfo = new AndroidNetInfo(mAndroidContext, mNetworkInterfaceInfo);
    }

    @Test
    public void getIp4Address_withWifiMacWifiNoIp_returnDefault_test() throws SocketException {
        final String defaultInterfaceName = "eth0";
        final String defaultIpAddress = "172.27.217.176";

        // *** Setup ***
        // 1. there is a wifi interface with a MAC address
        // 2. the wifi interface does *not* have an IP
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("D2:28:DD:89:27:8F");

        // 3. there is a default with an IP
        when(mNetworkInterfaceInfo.getIp4HostAddressByName(defaultInterfaceName))
                .thenReturn(defaultIpAddress);

        // *** Execute ***
        final String ip4Address = mAndroidNetInfo.getIp4Address(defaultInterfaceName);

        // *** Validate ***
        assertThat(ip4Address, is(equalTo(defaultIpAddress)));
    }

    //----
    // wifiMacAddress()
    //----
    @Test
    public void wifiMacAddress_goodMac_returnValid_test() {
        // SUCCESS - Context returns valid MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("1C-7C-D7-09-A3-DE");
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(equalTo(new BigInteger("31322509255646"))));
    }

    @Test
    public void wifiMacAddress_wifiManagerNull_returnNull_test() {
        // FAILURE - Context returns null on Context.WIFI_SERVICE
        when(mAndroidContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(null);
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    @Test
    public void wifiMacAddress_wifiInfoNull_returnNull_test() {
        // FAILURE - Context returns null on WifiInfo
        when(mAndroidWifiManager.getConnectionInfo()).thenReturn(null);
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    @Test
    public void wifiMacAddress_macAddressNull_returnNull_test() {
        // FAILURE - Context returns null on null MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn(null);
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    @Test
    public void wifiMacAddress_macEmpty_returnNull_test() {
        // FAILURE - Context returns null on null MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("");
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    @Test
    public void wifiMacAddress_macBadString_returnNull_test() {
        // FAILURE - Context returns null on null MAC address
        LOG.error("Please ignore the following exception.  Genereted by testing.");
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("BAD_MAC_ADDRESS_IGNORE_EXCEPTION");
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    //----
    // wifiInterfaceName()
    //----
    @Test
    public void wifiInterfaceName_goodMac_returnValid_test() throws SocketException {
        // SUCCESS - Context returns valid MAC address
        final String expectedName = "wlan76";
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("D8C2.2C61.EA55");
        when(mNetworkInterfaceInfo.getNameByMacAddress(new BigInteger("-43146496841131")))
                .thenReturn(expectedName);
        final String returnedName = mAndroidNetInfo.wifiInterfaceName();
        assertThat(returnedName, is(equalTo(expectedName)));
    }

    @Test
    public void wifiInterfaceName_nullMac_returnNull_test() throws SocketException {
        // FAILURE - Context returns valid MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn(null);
        when(mNetworkInterfaceInfo.getNameByMacAddress(any(BigInteger.class)))
                .thenReturn("something");
        final String returnedName = mAndroidNetInfo.wifiInterfaceName();
        assertThat(returnedName, is(nullValue()));
    }

    @Test
    public void wifiInterfaceName_nullName_returnNull_test() throws SocketException {
        // FAILURE - Context returns valid MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("4391983D0F98");
        when(mNetworkInterfaceInfo.getNameByMacAddress(any(BigInteger.class)))
                .thenReturn(null);
        final String returnedName = mAndroidNetInfo.wifiInterfaceName();
        assertThat(returnedName, is(nullValue()));
    }


}
