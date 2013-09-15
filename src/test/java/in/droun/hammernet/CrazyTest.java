/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import java.net.NetworkInterface;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collections;

import org.powermock.modules.junit4.PowerMockRunner;

import static in.droun.hammernet.NetworkInterfaceInfo.InterfaceQuery;

import static org.junit.Assert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNull.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 *
 * @author Pedro F. Hernandez <digitalrounin@gmail.com>
 */
@PrepareForTest(NetworkInterface.class)
@RunWith(PowerMockRunner.class)
public class CrazyTest {

    private transient InterfaceQuery mInterfaceQuery;

    @Test
    public void getHostAddressByName_nullGetByName_nullReturn_test()
            throws SocketException {
        // Setup
        final InterfaceQuery interfaceQuery = mock(InterfaceQuery.class);
        when(interfaceQuery.getByName("eth0")).thenReturn(null);
        final NetworkInterfaceInfo interfaceInfo = new NetworkInterfaceInfo(interfaceQuery);

        // execute
        final String result = interfaceInfo.getHostAddressByName("eth0", Inet4Address.class);

        // verify
        assertThat(result, is(nullValue()));
    }

    @Test
    public void getHostAddressByName_emptyGetInetAddresses_nullReturn_test()
            throws SocketException {
        // Setup
        final NetworkInterface mockInterface = mock(NetworkInterface.class);
        when(mockInterface.getInetAddresses())
                .thenReturn(Collections.<InetAddress>emptyEnumeration());

        final InterfaceQuery interfaceQuery = mock(InterfaceQuery.class);
        when(interfaceQuery.getByName("eth0")).thenReturn(mockInterface);

        final NetworkInterfaceInfo interfaceInfo = new NetworkInterfaceInfo(interfaceQuery);

        // execute
        final String result = interfaceInfo.getHostAddressByName("eth0", Inet4Address.class);

        // verify
        assertThat(result, is(nullValue()));
    }

}
