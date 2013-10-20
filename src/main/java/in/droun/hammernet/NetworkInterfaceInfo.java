/*
 * Copyright (c) 2013, Pedro F. Hernandez <digitalrounin@gmail.com>
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *
 * @author Pedro F. Hernandez <digitalrounin@gmail.com>
 */
public class NetworkInterfaceInfo {

    /**
     * Internal instance of {@link InterfaceQuery}.
     */
    private final transient InterfaceQuery mInterfaceQuery;

    public static final int MAX_INTERFACE_NAME_LENGTH = 128;

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
        String cleanMacString = null;
        if (isNotBlank(macString) && macString.length() <= maxMacStringLength) {

            // Cleanup and revalidate
            cleanMacString = macString.replaceAll("[:\\.\\s-]", "").toLowerCase();
            if (!cleanMacString.matches("[0-9a-f]{12}")) {
                cleanMacString = null;
            }
        }

        // Convert
        BigInteger result = null;
        if (isNotBlank(cleanMacString)) {
            final char[] macNibbles = cleanMacString.toCharArray();

            final int macByteSize = 6;
            final byte[] macAddress = new byte[macByteSize];
            for (int i = 0; i < macByteSize; i++) {
                final int pos = i * 2;
                macAddress[i] = Integer.decode("0x" + macNibbles[pos] + macNibbles[pos + 1])
                        .byteValue();
            }

            result = new BigInteger(macAddress);
        }

        return result;
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
     */
    // TODO: Remove SuppressWarnings
    @SuppressWarnings("PMD.SystemPrintln")
    public String getNameByMacAddress(final BigInteger macAddress) throws SocketException {

        // Fetch list of interfaces on the device and iterate
        final Enumeration<NetworkInterface> interfaces = mInterfaceQuery.getNetworkInterfaces();
        String result = null;
        if (macAddress != null && interfaces != null) {
            while (interfaces.hasMoreElements()) {
                final NetworkInterface current = interfaces.nextElement();

                byte[] hardwareAddress = null;
                try {
                    hardwareAddress = current.getHardwareAddress();
                } catch (SocketException socketException) {
                    System.err.println("Failed on: " + current.getName()
                            + " isVirtual:" + current.isVirtual()
                            + " isUp:" + current.isUp()
                            + " isPointToPoint:" + current.isPointToPoint()
                            );
                    throw socketException;
                }

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
        }

        return result;
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

        final NetworkInterface adaptor
                = isNotBlank(interfaceName) && interfaceName.length() <= MAX_INTERFACE_NAME_LENGTH
                ? mInterfaceQuery.getByName(interfaceName) : null;

        // Query for information requested
        String result = null;
        if (adaptor != null && clazz != null) {
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
