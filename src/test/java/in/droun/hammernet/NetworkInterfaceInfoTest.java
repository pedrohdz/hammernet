/*
 * Copyright (c) 2013, Pedro F. Hernandez <digitalrounin@gmail.com>
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import static org.junit.Assume.*;
import static org.mockito.Mockito.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import static in.droun.hammernet.NetworkInterfaceInfo.InterfaceQuery;
import java.math.BigInteger;

import java.net.Inet4Address;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

import org.junit.Test;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * This test is a bit of a hack considering that the data being used (sTestAdaptorName and
 * sTestAdaptorIp) were gotten from the method being tested. At least we are some what checking
 * consistency? Maybe it can be made to actually work in the future?
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class NetworkInterfaceInfoTest {

    private static String sTestAdaptorName;
    private static String sTestAdaptorIp;
    private static BigInteger sTestAdaptorMac;

    private transient NetworkInterfaceInfo mNetworkInterfaceInfo;
    private transient InterfaceQuery mInterfaceQuery;

    @BeforeClass
    @SuppressWarnings("PMD.SystemPrintln")
    public static void beforeClass() {
        String testAdaptorName = null;
        String testAdaptorIp = null;
        byte[] testAdaptorMac = null;
        try {
            final InterfaceQuery interfaceQuery = new InterfaceQuery();
            final Enumeration<NetworkInterface> networkInterfaces
                    = interfaceQuery.getNetworkInterfaces();
            final NetworkInterfaceInfo interfaceInfo = new NetworkInterfaceInfo();

            while (networkInterfaces.hasMoreElements()) {
                final NetworkInterface adaptor = networkInterfaces.nextElement();
                final String name = adaptor.getName();
                if (isNotBlank(name)) {
                    final String ipAddress = interfaceInfo.getIp4HostAddressByName(name);
                    if (isNotBlank(ipAddress)) {
                        testAdaptorName = name;
                        testAdaptorIp = ipAddress;
                        testAdaptorMac = adaptor.getHardwareAddress();
                        break;
                    }
                }
            }
        } catch (SocketException ex) {
            System.err.println("Problems setting up test adaptor information: " + ex.getMessage());
            testAdaptorName = null;
            testAdaptorIp = null;
        }

        sTestAdaptorName = testAdaptorName;
        sTestAdaptorIp = testAdaptorIp;
        sTestAdaptorMac = new BigInteger(testAdaptorMac);
    }

    @Before
    public void before() {
        // Setting up to use with Mockito
        mInterfaceQuery = spy(new InterfaceQuery());
        mNetworkInterfaceInfo = spy(new NetworkInterfaceInfo(mInterfaceQuery));
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert") // verify() is an assert
    public void getIp4HostAddressByName_callingGetHostAddressByName_true_test()
            throws SocketException {
        // Make sure that getIp4HostAddressByName() is calling getHostAddressByName()
        // This test should work even if sTestAdaptorName is null.
        mNetworkInterfaceInfo.getIp4HostAddressByName(sTestAdaptorName);
        verify(mNetworkInterfaceInfo, times(1))
                .getHostAddressByName(sTestAdaptorName, Inet4Address.class);
    }

    //----
    // getIp4HostAddressByName(String)
    //----
    @Test
    public void getIp4HostAddressByName_validName_validIp_test() throws SocketException {
        // Assuming we have sTestAdaptorName and sTestAdaptorIp, does it work?
        assumeThat(sTestAdaptorName, is(notNullValue()));
        assumeThat(sTestAdaptorIp, is(notNullValue()));
        final String ipAddress = mNetworkInterfaceInfo.getIp4HostAddressByName(sTestAdaptorName);
        assertThat(ipAddress, is(equalTo(sTestAdaptorIp)));
    }

    @Test
    public void getIp4HostAddressByName_nullName_nullIp_test() throws SocketException {
        // Null inteface name returns a null IP address
        final String ipAddress = mNetworkInterfaceInfo.getIp4HostAddressByName(null);
        verify(mInterfaceQuery, never()).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    @Test
    public void getIp4HostAddressByName_emptyName_nullIp_test() throws SocketException {
        // Empty string for an inteface name returns a null IP address
        final String ipAddress = mNetworkInterfaceInfo.getIp4HostAddressByName(null);
        verify(mInterfaceQuery, never()).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    @Test
    public void getIp4HostAddressByName_longName_nullIp_test() throws SocketException {
        // Too long of a string for an inteface name returns a null IP address
        final String ipAddress = mNetworkInterfaceInfo
                .getIp4HostAddressByName(StringUtils.repeat("x",
                NetworkInterfaceInfo.MAX_INTERFACE_NAME_LENGTH + 1));
        verify(mInterfaceQuery, never()).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    @Test
    public void getIp4HostAddressByName_goodName_nullIp_test() throws SocketException {
        // String just long enough for an inteface name returns a null IP address
        final String ipAddress = mNetworkInterfaceInfo
                .getIp4HostAddressByName(StringUtils.repeat("x",
                NetworkInterfaceInfo.MAX_INTERFACE_NAME_LENGTH));
        verify(mInterfaceQuery, times(1)).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    //----
    // getHostAddressByName(String, Class)
    //----
    @Test
    public void getHostAddressByName_goodName_nullIp_test() throws SocketException {
        // Good adaptor name, but null InetAddress.class
        final String ipAddress = mNetworkInterfaceInfo
                .getHostAddressByName(sTestAdaptorName, null);
        verify(mInterfaceQuery, times(1)).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    //----
    // getNameByMacAddress(BigInteger)
    //----
    @Test
    public void getNameByMacAddress_goodMac_goodResult_test() throws SocketException {
        // Assuming we have a MAC to test with, we get a good name
        assumeThat(sTestAdaptorMac, is(notNullValue()));
        assumeThat(sTestAdaptorName, is(notNullValue()));
        final String name = mNetworkInterfaceInfo.getNameByMacAddress(sTestAdaptorMac);
        assertThat(name, is(equalTo(sTestAdaptorName)));
    }

    @Test
    public void getNameByMacAddress_nullMac_nullResult_test() throws SocketException {
        // Null MAC results is null return
        final String name = mNetworkInterfaceInfo.getNameByMacAddress(null);
        assertThat(name, is(nullValue()));
    }

    @Test
    public void getNameByMacAddress_unknownMac_nullResult_test() throws SocketException {
        // NetworkInterfaces.getNetworkInterfaces() returns null results is null return
        final BigInteger unknownMac
                = NetworkInterfaceInfo.macAddressToBigInteger("18-78-13-8b-ec-9a");
        final String name = mNetworkInterfaceInfo.getNameByMacAddress(unknownMac);
        assertThat(name, is(nullValue()));
    }

    @Test
    public void getNameByMacAddress_nullInferfaceList_nullResult_test() throws SocketException {
        // NetworkInterfaces.getNetworkInterfaces() returns null results is null return
        when(mInterfaceQuery.getNetworkInterfaces()).thenReturn(null);
        final String name = mNetworkInterfaceInfo.getNameByMacAddress(sTestAdaptorMac);
        assertThat(name, is(nullValue()));
    }

}
