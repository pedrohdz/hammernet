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
import static org.mockito.Mockito.*;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.net.SocketException;
import org.junit.After;

import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author Pedro F. Hernandez (Digital Rounin)
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP") // This is test code and have to hardcode values
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

    @After
    public void after() {
        mNetworkInterfaceInfo = null;
        mAndroidWifiInfo = null;
        mAndroidWifiManager = null;
        mAndroidContext = null;
        mAndroidNetInfo = null;
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
}
