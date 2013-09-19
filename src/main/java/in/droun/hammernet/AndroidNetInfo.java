/*
 * Copyright (c) 2013, Pedro F. Hernandez <digitalrounin@gmail.com>
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.math.BigInteger;
import java.net.SocketException;

/**
 * This class is mostly here to allow for dependency injection and to simplify unit testing. Since
 * we have no way of inheriting from {@link  java.net.NetworkInterface} or
 * {@link  android.content.Context} This class provides a nice wrapper around both when it comes to
 * networking functionality.
 *
 * @author Pedro F. Hernandez <digitalrounin@gmail.com>
 */
public class AndroidNetInfo {

    /**
     * Android context to use for networking calls.
     */
    private final transient Context mAndroidContext;

    private final transient NetworkInterfaceInfo mInterfaceInfo;

    /**
     * Initialize with an Android Context.
     *
     * @param context
     * @param interfaceInfo
     */
    public AndroidNetInfo(final Context context, final NetworkInterfaceInfo interfaceInfo) {
        mAndroidContext = context;
        mInterfaceInfo = interfaceInfo;
    }

    public AndroidNetInfo(final Context context) {
        this(context, new NetworkInterfaceInfo());
    }

    /**
     *
     * Wrapper static method that allows for getting the IP address in a single call:
     *
     * <pre>
     *{@code
     *    final String ipAddress = AndroidNetInfo.wifiIp4Address(this);
     *}
     * </pre>
     *
     * @param context          {@link android.content.Context}
     *
     * @return IP address, or null if unable to obtain
     *
     * @throws SocketException
     */
    public static String wifiIp4Address(final Context context) throws SocketException {
        return wifiOrDefaultIp4Address(context, null);
    }

    /**
     *
     * Wrapper static method that allows for getting the IP address in a single call:
     *
     * <pre>
     *{@code
     *    final String defaultInterfaceName = BuildConstants.DEV_BUILD ? "eth0" : null;
     *    final String ipAddress
     *            = AndroidNetInfo.wifiOrDefaultIp4Address(this, defaultInterfaceName);
     *}
     * </pre>
     *
     * The <i>defaultInterface</i> for testing on emulators, where there is no wifi interface.
     *
     * @param context          {@link android.content.Context}
     * @param defaultInterface if unable to find wifi interface, it will return the IP address of
     *                         defaultInterfaceif instead
     *
     * @return IP address, or null if unable to obtain
     *
     * @throws SocketException
     */
    public static String wifiOrDefaultIp4Address(final Context context,
            final String defaultInterface) throws SocketException {
        final AndroidNetInfo androidNetInfo = new AndroidNetInfo(context);
        return androidNetInfo.getIp4Address(defaultInterface);
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
    public String wifiInterfaceName() throws SocketException {

        final BigInteger wifiMac = wifiMacAddress();
        final String interfaceName = wifiMac != null
                ? mInterfaceInfo.getNameByMacAddress(wifiMac) : null;
        return isNotBlank(interfaceName) ? interfaceName : null;
    }

    /**
     * Finds the devices WiFi interface and returns its MAC address.
     * <p>
     * This method tries it's best to always return null if any problems occur.
     *
     * @return The WiFi device's MAC address, null if there is no WiFi interface, or if there are
     *         any errors encountered while trying to obtain the MAC address.
     */
    public BigInteger wifiMacAddress() {

        // Get WiFi interface's MAC address as a BigInteger.
        final WifiManager wifiManager
                = (WifiManager) mAndroidContext.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager != null ? wifiManager.getConnectionInfo() : null;
        final String wifiMacString = wifiInfo != null ? wifiInfo.getMacAddress() : null;

        BigInteger result;
        if (isNotBlank(wifiMacString)) {
            result = NetworkInterfaceInfo.macAddressToBigInteger(wifiMacString);
        } else {
            result = null;
        }

        return result;
    }

    public String getIp4Address() throws SocketException {
        return getIp4Address(null);
    }

    public String getIp4Address(final String defaultInterface) throws SocketException {
        String ipAddress = null;
        // Find Wifi IP address
        String interfaceName = wifiInterfaceName();

        // Use defaultInterface if no wifi interface found
        if (isBlank(interfaceName)) {
            interfaceName = defaultInterface;
        }

        // Actually get the IP address
        if (isNotBlank(interfaceName)) {
            ipAddress = mInterfaceInfo.getIp4HostAddressByName(interfaceName);
        }

        return ipAddress;
    }
}
