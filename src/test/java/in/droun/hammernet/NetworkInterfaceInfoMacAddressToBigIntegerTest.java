/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import java.math.BigInteger;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Pedro F. Hernandez (Digital Rounin)
 */
@RunWith(Parameterized.class)
public class NetworkInterfaceInfoMacAddressToBigIntegerTest {

    @Parameters(name = "{index}: f({0})={1}")
    public static Iterable<Object[]> data() {
        return NetworkInterfaceInfoData
                .macAddressToByteArrayData();
    }

    private final transient BigInteger mExpected;
    private final transient String mInput;

    public NetworkInterfaceInfoMacAddressToBigIntegerTest(final String input,
            final BigInteger expected) {
        mInput = input;
        mExpected = expected;
    }

    @Test
    public void macAddressToBigInteger_validInput_validOutput_test() {
        assertThat(NetworkInterfaceInfo.macAddressToBigInteger(mInput), is(equalTo(mExpected)));
    }

}
