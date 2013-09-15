/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Pedro F. Hernandez (Digital Rounin)
 */
public class Iface {

    /**
     * Internal instance of {@link InterfaceQuery}.
     */
    private final transient InterfaceQuery mInterfaceQuery;

    /**
     * Simple NetworkInterfaceInfo constructor.
     */
    public Iface() {
        this(new InterfaceQuery());
    }

    /**
     * This constructor is only intended to aid with unit testing. This constructor allows for an
     * instance of {@link InterfaceQuery} to be injected, making it easier to perform testing.
     *
     * @param interfaceQuery instance of {@link  InterfaceQuery}.
     */
    protected Iface(final InterfaceQuery interfaceQuery) {
        mInterfaceQuery = interfaceQuery;
    }

    public IfaceData getByMacAddress(final BigInteger macAddress) {
        throw new NotImplementedException();
    }

    public IfaceData getByIp4Address(final String ipV4Address) {
        throw new NotImplementedException();
    }

    /**
     * Returns the numeric representation of this IP address (such as "127.0.0.1"). Can return
     * either IPv4 or IPv6, depending on value of clazz.
     *
     * @param interfaceName name of interface.
     *
     * @return Interface's IP address
     *
     * @throws SocketException if a network error occurs.
     */
    public String getByIfaceName(final String interfaceName)
            throws SocketException {

        // Validate
        final int maxInterfaceNameSize = 128;
        if (interfaceName == null || interfaceName.isEmpty()
                || maxInterfaceNameSize > maxInterfaceNameSize) {
            throw new IllegalArgumentException("'interfaceName' is either empty or null.");
        }

        // Query for information requested
        final NetworkInterface adaptor = mInterfaceQuery.getByName(interfaceName);
        String result = null;
        if (adaptor != null) {
            final Enumeration<InetAddress> addresses = adaptor.getInetAddresses();
            while (addresses.hasMoreElements()) {
                final InetAddress currentAddress = addresses.nextElement();
                if (currentAddress != null && currentAddress instanceof Inet4Address) {
                    result = currentAddress.getHostAddress();
                    break;
                }
            }
        }
        return result;
    }

    /**
     * This internal class is only intended to be used in unit testing. It wraps around
     * {@link java.net.NetworkInterface}. It basically allows for easier mocking.
     */
    protected static class InterfaceQuery {

        /**
         * A wrapper around {@link java.net.NetworkInterface#getByName(java.lang.String)}.
         *
         * @param name
         *
         * @return
         *
         * @throws SocketException
         */
        protected NetworkInterface getByName(final String name) throws SocketException {
            return NetworkInterface.getByName(name);
        }

        /**
         * A wrapper around {@link java.net.NetworkInterface#getNetworkInterfaces()}.
         *
         * @return
         *
         * @throws SocketException
         */
        protected Enumeration<NetworkInterface> getNetworkInterfaces() throws SocketException {
            return NetworkInterface.getNetworkInterfaces();
        }
    }
}
