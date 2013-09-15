/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import java.math.BigInteger;

/**
 *
 * @author Pedro F. Hernandez (Digital Rounin)
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize") // Accessors have slighting different names
public class IfaceDataImp implements IfaceData {
    private String mName;
    private String mDisplayName;
    private BigInteger mMacAddress;
    private String mIp4Address;

    protected IfaceDataImp() {
    }

    @Override
    public String getName() {
        return mName;
    }

    protected IfaceDataImp setName(final String name) {
        this.mName = name;
        return this;
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    protected IfaceDataImp setDisplayName(final String displayName) {
        this.mDisplayName = displayName;
        return this;
    }

    @Override
    public BigInteger getMacAddress() {
        return mMacAddress;
    }

    protected IfaceDataImp setMacAddress(final BigInteger macAddress) {
        this.mMacAddress = macAddress;
        return this;
    }

    @Override
    public String getIp4Address() {
        return mIp4Address;
    }

    protected IfaceDataImp setIpV4Address(final String ip4Address) {
        this.mIp4Address = ip4Address;
        return this;
    }
}
