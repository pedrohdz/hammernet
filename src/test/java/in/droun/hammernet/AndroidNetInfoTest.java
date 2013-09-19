/*
 * Copyright (c) 2013, Pedro F. Hernandez <digitalrounin@gmail.com>
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
import static in.droun.hammernet.NetworkInterfaceInfo.macAddressToBigInteger;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.math.BigInteger;
import java.net.SocketException;

import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Pedro F. Hernandez <digitalrounin@gmail.com>
 */
@SuppressWarnings({ "PMD.AvoidUsingHardCodedIP", // This is test code and have to hardcode values
    "PMD.TooManyStaticImports" }) // Again, test code, I am going to use imports a lot.
public class AndroidNetInfoTest {

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

    //----
    // getIp4Address()
    //----
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // verify() is an assert
    public void getIp4Address_callsOther_true_test() throws SocketException {
        // SUCCESS - Make sure that getIp4Address() getIp4Address(String)
        final AndroidNetInfo androidNetInfo = spy(new AndroidNetInfo(mAndroidContext));
        androidNetInfo.getIp4Address();
        verify(androidNetInfo, times(1)).getIp4Address(anyString());
    }

    //----
    // getIp4Address(String)
    //----
    @Test
    public void getIp4Address_withValid_returnWifiIp_test() throws SocketException {
        // SUCCESS - Works as expected
        final String wifiInterfaceName = "wlan0utest";
        final String wifiIpAddress = "172.25.25.176";
        final String wifiMacString = "50:d1:5f:4e:be:75";

        // Arrange
        when(mAndroidWifiInfo.getMacAddress()).thenReturn(wifiMacString);
        when(mNetworkInterfaceInfo.getNameByMacAddress(macAddressToBigInteger(wifiMacString)))
                .thenReturn(wifiInterfaceName);
        when(mNetworkInterfaceInfo.getIp4HostAddressByName(wifiInterfaceName))
                .thenReturn(wifiIpAddress);

        // Act
        final String ip4Address = mAndroidNetInfo.getIp4Address("TEST_DEFAULT");

        // Assert
        assertThat(ip4Address, is(equalTo(wifiIpAddress)));
    }

    @Test
    public void getIp4Address_withWifiMacWifiNoIp_returnDefault_test() throws SocketException {
        // SUCCESS - No wifi found, default found and returned
        final String defaultInterfaceName = "eth0utest";
        final String defaultIpAddress = "172.27.217.176";

        // Arrange
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("D2:28:DD:89:27:8F");
        when(mNetworkInterfaceInfo.getIp4HostAddressByName(defaultInterfaceName))
                .thenReturn(defaultIpAddress);

        // Act
        final String ip4Address = mAndroidNetInfo.getIp4Address(defaultInterfaceName);

        // Assert
        assertThat(ip4Address, is(equalTo(defaultIpAddress)));
    }

    @Test
    public void getIp4Address_noWifiNoDefaultFound_returnNull_test() throws SocketException {
        // FAILURE - No wifi or default interface found, return null
        final String ip4Address = mAndroidNetInfo.getIp4Address("p2putest");
        assertThat(ip4Address, is(nullValue()));
    }

    @Test
    public void getIp4Address_noWifiNoDefaultGiven_returnNull_test() throws SocketException {
        // FAILURE - No wifi found, no default given, return null
        final String ip4Address = mAndroidNetInfo.getIp4Address(null);
        assertThat(ip4Address, is(nullValue()));
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
        // FAILURE - Context returns null on empty string MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("");
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    @Test
    public void wifiMacAddress_macBadString_returnNull_test() {
        // FAILURE - Context returns null on bad MAC address
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("BAD_MAC_ADDRESS_IGNORE_EXCEPTION");
        final BigInteger wifiMacAddress = mAndroidNetInfo.wifiMacAddress();
        assertThat(wifiMacAddress, is(nullValue()));
    }

    //----
    // wifiInterfaceName()
    //----
    @Test
    public void wifiInterfaceName_goodMac_returnValid_test() throws SocketException {
        // SUCCESS - Able to locate the wifi interface
        final String expectedName = "wlan76";
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("D8C2.2C61.EA55");
        when(mNetworkInterfaceInfo.getNameByMacAddress(new BigInteger("-43146496841131")))
                .thenReturn(expectedName);
        final String returnedName = mAndroidNetInfo.wifiInterfaceName();
        assertThat(returnedName, is(equalTo(expectedName)));
    }

    @Test
    public void wifiInterfaceName_nullMac_returnNull_test() throws SocketException {
        // FAILURE - WifiInfo returns null MAC address, return null
        when(mAndroidWifiInfo.getMacAddress()).thenReturn(null);
        // Force to fail if called
        when(mNetworkInterfaceInfo.getNameByMacAddress(any(BigInteger.class)))
                .thenReturn("something");
        final String returnedName = mAndroidNetInfo.wifiInterfaceName();
        assertThat(returnedName, is(nullValue()));
    }

    @Test
    public void wifiInterfaceName_nullName_returnNull_test() throws SocketException {
        // FAILURE - NetworkInterfaceInfo cannot find the interface, return null
        when(mAndroidWifiInfo.getMacAddress()).thenReturn("4391983D0F98");
        when(mNetworkInterfaceInfo.getNameByMacAddress(any(BigInteger.class)))
                .thenReturn(null);
        final String returnedName = mAndroidNetInfo.wifiInterfaceName();
        assertThat(returnedName, is(nullValue()));
    }
}
