package org.magicdgs.hdf5j.internal.address;

import org.magicdgs.hdf5j.HDF5jTest;
import org.magicdgs.hdf5j.fileformat.FileAddress;
import org.magicdgs.hdf5j.utils.exceptions.FileAddressException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressImplUnitTest extends HDF5jTest {

    @Test
    public void testSimpleAddress() throws Exception {
        // encode the same address (10) with long and int
        final FileAddressImpl longAddress =
                new FileAddressImpl(ByteBuffer.allocate(Long.BYTES).putLong(10).array());
        final FileAddressImpl intAddress =
                new FileAddressImpl(ByteBuffer.allocate(Integer.BYTES).putInt(10).array());
        // assert that the files are equal because of the position
        Assert.assertEquals(longAddress.getFilePointer(), 10);
        Assert.assertEquals(intAddress.getFilePointer(), 10);
        Assert.assertEquals(longAddress, intAddress);
        // and in this case, they also have the same hashCode
        Assert.assertEquals(longAddress.hashCode(), intAddress.hashCode());
        // in addition, check that the equals for the same objects are working
        Assert.assertEquals(longAddress, longAddress);
        Assert.assertEquals(intAddress, intAddress);
        // check the string method too
        Assert.assertEquals(longAddress.hexDisplay(), "FileAddress[0x000000000000000a]");
        Assert.assertEquals(intAddress.hexDisplay(), "FileAddress[0x0000000a]");
    }

    @Test
    public void testMaximumAddress() throws Exception {
        final FileAddress address = new FileAddressImpl(
                ByteBuffer.allocate(Long.BYTES).putLong(Long.MAX_VALUE).array());
        Assert.assertEquals(address.getFilePointer(), Long.MAX_VALUE);
    }

    @Test
    public void testNonEqualAddresses() throws Exception {
        final FileAddress first = new FileAddressImpl(new byte[] {-1, -1, -1, -1});
        Assert.assertFalse(first.equals("FileAddressImpl"));
        Assert.assertFalse(first.equals(new FileAddressImpl(new byte[] {1, 1, 1, 1})));
    }

    @Test
    public void tesUndefinedAddress() throws Exception {
        final FileAddress undefined = FileAddressImpl.getUndefinedAddressForSize(Integer.BYTES);
        Assert.assertEquals(undefined.getFilePointer(), -1);
        Assert.assertEquals(undefined.hexDisplay(), "FileAddress[0xffffffff]");
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
        new FileAddressImpl(bytes);
    }

    @DataProvider
    public Object[][] invalidAddress() {
        return new Object[][] {
                {ByteBuffer.allocate(Long.BYTES).putLong(-2).array()},
                {ByteBuffer.allocate(Long.BYTES).putLong(-10).array()}
        };
    }

    @Test(dataProvider = "invalidAddress", expectedExceptions = FileAddressException.class)
    public void testInvalidFileAddress(final byte[] bytes) throws Exception {
        new FileAddressImpl(bytes);
    }
}