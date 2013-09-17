/*
 * Copyright (c) 2013, Pedro F. Hernandez (Digital Rounin)
 *
 * All rights reserved.
 *
 * See the separate "LICENSE.md" file for the distribution license (Modified BSD licence)
 */
package in.droun.hammernet;

import java.math.BigInteger;
import java.util.Arrays;

public final class NetworkInterfaceInfoData {

    private NetworkInterfaceInfoData() {
    }

    // CHECKSTYLE.OFF: MethodLengthCheck - Chock full of test data
    public static Iterable<Object[]> macAddressToByteArrayData() {
        final BigInteger sameMac = new BigInteger("-9964451978704");
        final Object[][] values = {
            //----
            // Good values
            //----
            // Known good
            { "56:67:78:89:45:34", new BigInteger("95002403882292") },
            // Variations of f6:ef:f8:61:22:30 must be all equal to -9964451978704
            { "F6-EF-F8-61-22-30", sameMac },
            { "f6-ef-f8-61-22-30", sameMac },
            { "F6:EF:F8:61:22:30", sameMac },
            { "f6:ef:f8:61:22:30", sameMac },
            { "F6EF.F861.2230", sameMac },
            { "f6ef.f861.2230", sameMac },
            { "F6EFF8612230", sameMac },
            { "f6eff8612230", sameMac },
            // Used later in failure testing
            { "56:a7:78:89:4f:34", new BigInteger("95277281791796") },
            // Random test values
            { "DD-C3-3E-DB-4C-29", new BigInteger("-37644333790167") },
            { "67-19-68-a2-02-c0", new BigInteger("113358827291328") },
            { "7f:a7:61:49:a4:32", new BigInteger("140356868482098") },
            { "d9:cc:db:e6:ae:b3", new BigInteger("-42001090826573") },
            { "439C.0AB2.0D70", new BigInteger("74337473400176") },
            { "3bba.312a.ea81", new BigInteger("65670874851969") },
            { "FAD080347197", new BigInteger("-5701565648489") },
            { "84b95172e33b", new BigInteger("-135543506410693") },
            //----
            // Bad values
            //----
            // null
            { null, null },
            // empty
            { "", null },
            // too long with delimitter
            { "56:a7:78:89:4f:34:", null },
            // one too short after strip
            { "56677889453", null },
            // too short
            { "1111111111111111", null },
            // way too short
            { "2", null },
            // too long numbers
            { "999999999999999999", null },
            // right size, extra delimitter (too short)
            { "56:a7:78::9:4f:34", null }, //NOPMD
            // right size all spaces
            { "                 ", null },
            // invalid char ($)
            { "56:a7$78:89:4f:34", null },
            // invalid char (g)
            { "56:a7:78:g9:4f:34", null }
        };

        return Arrays.asList(values);
    }
    // CHECKSTYLE.ON: MethodLengthCheck
}
