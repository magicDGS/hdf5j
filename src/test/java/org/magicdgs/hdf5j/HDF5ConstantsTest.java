package org.magicdgs.hdf5j;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class HDF5ConstantsTest extends HDF5jTest {

    @Test
    public void testFormatSignature() {
        // check that the signature has 8 bytes
        Assert.assertEquals(HDF5Constants.HDF5_FORMAT_SIGNATURE.length, 8);
        // assert that the signature contains the expected decimal representation
        Assert.assertEquals(HDF5Constants.HDF5_FORMAT_SIGNATURE, new int[]{137, 72, 68, 70, 13, 10, 26, 10});
    }

}