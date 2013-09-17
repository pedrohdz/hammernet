/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
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

@SuppressWarnings({ "PMD.SystemPrintln", "PMD.TooManyStaticImports" })
public class NetworkInterfaceInfoTest {

    private static final String TEST_ADAPTOR_NAME;
    private static final String TEST_ADAPTOR_IP;
    private static final BigInteger TEST_ADAPTOR_MAC;

    private transient NetworkInterfaceInfo mNetworkInterfaceInfo;
    private transient InterfaceQuery mInterfaceQuery;

    static {
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

        TEST_ADAPTOR_NAME = testAdaptorName;
        TEST_ADAPTOR_IP = testAdaptorIp;
        TEST_ADAPTOR_MAC = new BigInteger(testAdaptorMac);
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
        // This test should work even if TEST_ADAPTOR_NAME is null.
        mNetworkInterfaceInfo.getIp4HostAddressByName(TEST_ADAPTOR_NAME);
        verify(mNetworkInterfaceInfo, times(1))
                .getHostAddressByName(TEST_ADAPTOR_NAME, Inet4Address.class);
    }

    //----
    // getIp4HostAddressByName(String)
    //----
    /**
     * This test is a bit of a hack considering that the data being used (TEST_ADAPTOR_NAME and
     * TEST_ADAPTOR_IP) were gotten from the method being tested. At least we are some what checking
     * consistency? Maybe it can be made to actually work in the future?
     *
     * @throws SocketException
     */
    @Test
    public void getIp4HostAddressByName_validName_validIp_test() throws SocketException {
        // Assuming we have TEST_ADAPTOR_NAME and TEST_ADAPTOR_IP, does it work?
        assumeThat(TEST_ADAPTOR_NAME, is(notNullValue()));
        assumeThat(TEST_ADAPTOR_IP, is(notNullValue()));
        final String ipAddress = mNetworkInterfaceInfo.getIp4HostAddressByName(TEST_ADAPTOR_NAME);
        assertThat(ipAddress, is(equalTo(TEST_ADAPTOR_IP)));
    }

    /**
     * This is another hack. Checking to make sure that InterfaceQuery.getByName() is never called
     * as a way of making sure that execution stops, more or less. Legacy code like NetworkInterface
     * makes life very painful.
     *
     * @throws SocketException
     */
    @Test
    public void getIp4HostAddressByName_nullName_nullIp_test() throws SocketException {
        // Null inteface name returns a null IP address
        final String ipAddress = mNetworkInterfaceInfo.getIp4HostAddressByName(null);
        verify(mInterfaceQuery, never()).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    /**
     * This is another hack. Checking to make sure that InterfaceQuery.getByName() is never called
     * as a way of making sure that execution stops, more or less. Legacy code like NetworkInterface
     * makes life very painful.
     *
     * @throws SocketException
     */
    @Test
    public void getIp4HostAddressByName_emptyName_nullIp_test() throws SocketException {
        // Empty string for an inteface name returns a null IP address
        final String ipAddress = mNetworkInterfaceInfo.getIp4HostAddressByName(null);
        verify(mInterfaceQuery, never()).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    /**
     * This is another hack. Checking to make sure that InterfaceQuery.getByName() is never called
     * as a way of making sure that execution stops, more or less. Legacy code like NetworkInterface
     * makes life very painful.
     *
     * @throws SocketException
     */
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
                .getHostAddressByName(TEST_ADAPTOR_NAME, null);
        verify(mInterfaceQuery, times(1)).getByName(anyString());
        assertThat(ipAddress, is(nullValue()));
    }

    //----
    // getNameByMacAddress(BigInteger)
    //----
    @Test
    public void getNameByMacAddress_goodMac_goodResult_test() throws SocketException {
        // Assuming we have a MAC to test with, we get a good name
        assumeThat(TEST_ADAPTOR_MAC, is(notNullValue()));
        assumeThat(TEST_ADAPTOR_NAME, is(notNullValue()));
        final String name = mNetworkInterfaceInfo.getNameByMacAddress(TEST_ADAPTOR_MAC);
        assertThat(name, is(equalTo(TEST_ADAPTOR_NAME)));
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
        final String name = mNetworkInterfaceInfo.getNameByMacAddress(TEST_ADAPTOR_MAC);
        assertThat(name, is(nullValue()));
    }

}
