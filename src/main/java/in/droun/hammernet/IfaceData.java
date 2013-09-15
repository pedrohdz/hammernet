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
public interface IfaceData {

    String getDisplayName();

    String getIp4Address();

    BigInteger getMacAddress();

    String getName();
}
