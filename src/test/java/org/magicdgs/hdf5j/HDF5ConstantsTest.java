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
        Assert.assertEquals(HDF5Constants.FORMAT_SIGNATURE.length, 8);
        // assert that the signature contains the expected String as a char array
        Assert.assertEquals(HDF5Constants.FORMAT_SIGNATURE, "\211HDF\r\n\032\n".chars().toArray());
    }

}