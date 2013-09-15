/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import org.junit.Test;
import java.math.BigInteger;
import java.util.Arrays;
import java.net.SocketException;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.junit.Before;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.After;

//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;//import static org.mockito.Mockito.*;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
@RunWith(Enclosed.class)
public class NetworkInterfaceInfoTest {

    @Test
    public void testWifiMacAddress() {
        fail("The test case is a prototype.");
    }

    @RunWith(Parameterized.class)
    public static class MacAddressToByteArrayValidParameters {

        @Parameters(name = "{index}: f({0})={1}")
        public static Iterable<Object[]> data() {
            return NetworkInterfaceInfoTestData.macAddressToByteArrayValidParameters();
        }

        private final transient String mInput;
        private final transient BigInteger mExpected;

        public MacAddressToByteArrayValidParameters(final String input, final BigInteger expected) {
            mInput = input;
            mExpected = expected;
        }

        @Test
        public void macAddressToBigInteger_validInput_validOutput_test() {
            assertThat(NetworkInterfaceInfo.macAddressToBigInteger(mInput), is(equalTo(mExpected)));
        }
    }

    @RunWith(Parameterized.class)
    public static class MacAddressToByteArrayIllegalArgumentException {

        @Parameters(name = "{index}: f({0})")
        public static Iterable<Object[]> data() {
            return NetworkInterfaceInfoTestData.macAddressToByteArrayInvalidParameters();
        }

        private final transient String mInput;

        public MacAddressToByteArrayIllegalArgumentException(final String input) {
            mInput = input;
        }

        @Test(expected = IllegalArgumentException.class)
        public void macAddressToBigInteger_invalidInput_illegalArgumentException_test() {
            NetworkInterfaceInfo.macAddressToBigInteger(mInput);
        }
    }

    @RunWith(Parameterized.class)
    public static class GetHostAddressByNameIllegalArgumentException {

        private transient NetworkInterfaceInfo mInterfaceInfo;

        private final transient String mInterfaceName;
        private final transient Class mClazz;

        @Before
        public void before() {
            mInterfaceInfo = new NetworkInterfaceInfo(null);
        }

        @After
        public void after() {
            mInterfaceInfo = null;
        }

        @Parameters(name = "{index} f({0}, {1})")
        public static Iterable<Object[]> data() {
            final Object[][] values = {
                { null, null },
                { "", null }, };

            return Arrays.asList(values);
        }

        public GetHostAddressByNameIllegalArgumentException(final String interfaceName,
                final Class clazz) {
            mInterfaceName = interfaceName;
            mClazz = clazz;
        }

        @Test(expected = IllegalArgumentException.class)
        public void getHostAddressByName_invalidInput_illegalArgumentException_test()
                throws SocketException {
            mInterfaceInfo.getHostAddressByName(mInterfaceName, mClazz);
        }
    }
}
