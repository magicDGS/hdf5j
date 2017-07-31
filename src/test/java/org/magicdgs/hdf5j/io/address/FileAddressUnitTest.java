package org.magicdgs.hdf5j.io.address;

import org.magicdgs.hdf5j.utils.HDF5jException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressUnitTest {

    @Test
    public void testSimpleAddress() throws Exception {
        // encode the same address (10) with long and int
        final FileAddress longAddress =
                new FileAddress(ByteBuffer.allocate(Long.BYTES).putLong(10).array());
        final FileAddress intAddress =
                new FileAddress(ByteBuffer.allocate(Integer.BYTES).putInt(10).array());
        // assert that the files are equal because of the position
        Assert.assertEquals(longAddress.position, 10);
        Assert.assertEquals(intAddress.position, 10);
        Assert.assertEquals(longAddress, intAddress);
        // and in this case, they also have the same hashCode
        Assert.assertEquals(longAddress.hashCode(), intAddress.hashCode());
        // in addition, check that the equals for the same objects are working
        Assert.assertEquals(longAddress, longAddress);
        Assert.assertEquals(intAddress, intAddress);
        // check the string method too
        Assert.assertEquals(longAddress.toString(), "FileAddress[8]:0xa");
        Assert.assertEquals(intAddress.toString(), "FileAddress[4]:0xa");
    }

    @Test
    public void testMaximumAddress() throws Exception {
        final FileAddress address =
                new FileAddress(ByteBuffer.allocate(Long.BYTES).putLong(Long.MAX_VALUE).array());
        Assert.assertEquals(address.position, Long.MAX_VALUE);
    }

    @Test
    public void testNonEqualAddresses() throws Exception {
        final FileAddress first = new FileAddress(new byte[] {-1, -1, -1, -1});
        Assert.assertFalse(first.equals("FileAddress"));
        Assert.assertFalse(first.equals(new FileAddress(new byte[] {1, 1, 1, 1})));
    }

    @Test
    public void tesUndefinedAddress() throws Exception {
        final FileAddress undefined =
                new FileAddress(ByteBuffer.allocate(Integer.BYTES).putInt(-1).array());
        Assert.assertEquals(undefined.position, -1);
        Assert.assertEquals(undefined.toString(), "UndefinedFileAddress[4]:0xffffffffffffffff");
    }


    @DataProvider
    public Object[][] illegalArguments() {
        return new Object[][] {
                {null},
                {new byte[0]}
        };
    }

    @Test(dataProvider = "illegalArguments", expectedExceptions = IllegalArgumentException.class)
    public void testIllegalArguments(final byte[] bytes) throws Exception {
        new FileAddress(bytes);
    }

    @DataProvider
    public Object[][] invalidAddress() {
        return new Object[][] {
                {ByteBuffer.allocate(Long.BYTES).putLong(-2).array()},
                {ByteBuffer.allocate(Long.BYTES).putLong(-10).array()}
        };
    }

    @Test(dataProvider = "invalidAddress", expectedExceptions = HDF5jException.FileAddressException.class)
    public void testInvalidFileAddress(final byte[] bytes) throws Exception {
        new FileAddress(bytes);
    }

}