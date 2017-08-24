package org.magicdgs.hdf5j.utils.hashing;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class Lookup3HashOnTheFlyTest {

    @Test(dataProvider = "bytesToHash", dataProviderClass = Lookup3HashFunctionTest.class)
    public void testHashBytes(byte[] input, int k, int len, final int expectedHash)
            throws Exception {
        final Lookup3HashOnTheFly lookup3HashOnTheFly = new Lookup3HashOnTheFly(len - k, 0);
        for (int i = k; i < len; i++) {
            lookup3HashOnTheFly.update(input[i]);
        }
        Assert.assertEquals(lookup3HashOnTheFly.finalizeHash(), expectedHash);
    }

}