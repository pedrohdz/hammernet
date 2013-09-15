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

/**
 *
 * @author Pedro F. Hernandez (Digital Rounin)
 */
public class NetworkInterfaceInfo {

    /**
     * Internal instance of {@link InterfaceQuery}.
     */
    private final transient InterfaceQuery mInterfaceQuery;

    /**
     * Simple NetworkInterfaceInfo constructor.
     */
    public NetworkInterfaceInfo() {
        this(new InterfaceQuery());
    }

    /**
     * This constructor is only intended to aid with unit testing. This constructor allows for an
     * instance of {@link InterfaceQuery} to be injected, making it easier to perform testing.
     *
     * @param interfaceQuery instance of {@link  InterfaceQuery}.
     */
    protected NetworkInterfaceInfo(final InterfaceQuery interfaceQuery) {
        mInterfaceQuery = interfaceQuery;
    }

    /**
     * Converts a MAC address string to byte array.
     *
     * @param macString Any MAC address in string form, bytes delimited by either ':', '-', or ' '.
     *
     * @return BigInteger of given MAC address.
     */
    public static BigInteger macAddressToBigInteger(final String macString) {
        // Validate
        final int maxMacStringLength = 17;
        if (macString == null || macString.isEmpty() || macString.length() > maxMacStringLength) {
            throw new IllegalArgumentException("Invalid maxMacStringLength. Either null, empty, "
                    + "or too long.");
        }

        // Cleanup and revalidate
        final String cleanMacString = macString.replaceAll("[:\\.\\s-]", "").toLowerCase();
        if (!cleanMacString.matches("[0-9a-f]{12}")) {
            throw new IllegalArgumentException("Invalid maxMacStringLength. Either invalid "
                    + "character, or wrong size.");
        }

        // Convert
        final String[] mac = cleanMacString.split("(?<=\\G.{2})");
        final int macByteSize = 6;
        final byte[] macAddress = new byte[macByteSize];
        for (int i = 0; i < mac.length; i++) {
            macAddress[i] = Integer.decode("0x" + mac[i]).byteValue();
        }
        return new BigInteger(macAddress);
    }

    /**
     * Returns the numeric representation of this IPv4 address (such as
     * "2001:0db8:85a3:0000:0000:8a2e:0370:7334").
     *
     * @param interfaceName name of interface.
     *
     * @return Interface's IP address
     *
     * @throws SocketException if a network error occurs.
     */
    public String getIp4HostAddressByName(final String interfaceName) throws SocketException {
        return getHostAddressByName(interfaceName, Inet4Address.class);
    }

    /**
     * Returns the Wi-Fi interface name on an Android device.
     *
     * @return The interface name of the Wi-Fi adapter. If there is no Wi-Fi adapter, null is
     *         returned. This is typical the case for Android Virtual Machines.
     *
     * @throws SocketException
     *
     * <!-- CHECKSTYLE.OFF: LineLength - URLs, cannot shorten -->
     * @see <a
     * href="http://stackoverflow.com/questions/4677684/wifi-network-interface-name/18657669#18657669">
     * Stackoverflow: wifi network interface name</a> - for more information.
     * <!-- CHECKSTYLE.ON: LineLength -->
     *
     *
     */
    public String getNameByMacAddress(final BigInteger macAddress) throws SocketException {

        if (macAddress == null) {
            throw new IllegalArgumentException("macAddress is null");
        }

        // Fetch list of interfaces on the device and iterate
        String result = null;
        final Enumeration<NetworkInterface> interfaces = mInterfaceQuery.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            final NetworkInterface current = interfaces.nextElement();

            final byte[] hardwareAddress = current.getHardwareAddress();
            if (hardwareAddress == null) {
                continue;
            }

            // Next NOPMD is for AvoidInstantiatingObjectsInLoops, no choice in the matter
            final BigInteger currentMac = new BigInteger(hardwareAddress); // NOPMD
            if (currentMac.equals(macAddress)) {
                // If the current interface's and WiFi MAC match, we have a winner
                result = current.getName();
                break;
            }
        }

        return result;
    }


    /**
     * Returns the numeric representation of this IP address (such as "127.0.0.1"). Can return
     * either IPv4 or IPv6, depending on value of clazz.
     *
     * @param interfaceName name of interface.
     * @param clazz         type of address to return. Either Inet4Address or Inet4Address. Returns
     *                      null if not found.
     *
     * @return Interface's IP address
     *
     * @throws SocketException if a network error occurs.
     */
    protected String getHostAddressByName(final String interfaceName, final Class clazz)
            throws SocketException {

        // Validate
        final int maxInterfaceNameSize = 128;
        if (interfaceName == null || interfaceName.isEmpty()
                || maxInterfaceNameSize > maxInterfaceNameSize) {
            throw new IllegalArgumentException("'interfaceName' is either empty or null.");
        }

        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' is null.");
        }

        // Query for information requested
        final NetworkInterface adaptor = mInterfaceQuery.getByName(interfaceName);
        String result = null;
        if (adaptor != null) {
            final Enumeration<InetAddress> addresses = adaptor.getInetAddresses();
            while (addresses.hasMoreElements()) {
                final InetAddress currentAddress = addresses.nextElement();
                if (currentAddress != null && clazz.isInstance(currentAddress)) {
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
