package org.magicdgs.hdf5j.io.address;

import org.magicdgs.hdf5j.utils.HDF5jException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressManagerUnitTest {

    @Test
    public void testValidateAddress() throws Exception {
        final FileAddressManager manager = new FileAddressManager(10);
        // check that do not throw any exception for the same number of bytes
        manager.validateAddress(new FileAddress(new byte[10]));
        // do not throw an exception for less bytes
        manager.validateAddress(new FileAddress(new byte[5]));
    }

    @DataProvider
    public Object[][] undefineAddressSizes() {
        return new Object[][] {{1}, {5}, {10}, {20}};
    }

    @Test(dataProvider = "undefineAddressSizes")
    public void testUndefinedAddress(final int size) throws Exception {
        FileAddressManager manager = new FileAddressManager(size);
        Assert.assertEquals(manager.getUndefinedAddress().bytes.length, size);
        Assert.assertEquals(manager.getUndefinedAddress().position, -1);
    }

    @DataProvider
    public Object[][] buffersToDecode() {
        final int position = 189765;
        return new Object[][] {
                // try with int, long and double
                {Integer.BYTES, ByteBuffer.allocate(Integer.BYTES).putInt(position), position},
                {Long.BYTES, ByteBuffer.allocate(Long.BYTES).putLong(position), position}
        };
    }

    @Test(dataProvider = "buffersToDecode")
    public void testDecodeAddress(final int size, final ByteBuffer buffer, final long position) {
        // rewinds the buffer to read from the beginning
        buffer.rewind();
        final FileAddressManager manager = new FileAddressManager(size);
        final FileAddress address = manager.decodeAddress(buffer);
        Assert.assertEquals(address.bytes.length, size);
        Assert.assertEquals(address.position, position);
        // check that the buffer is consumed
        Assert.assertFalse(buffer.hasRemaining());
    }

    @Test
    public void testEncodeAddress() throws Exception {
        final int position = 10;
        // encode first as a int
        FileAddressManager manager = new FileAddressManager(Integer.BYTES);
        final FileAddress address = new FileAddress(
                ByteBuffer.allocate(manager.getAddressSize()).putInt(position).array());
        ByteBuffer buffer =
                manager.encodeAddress(address, ByteBuffer.allocate(manager.getAddressSize()));
        Assert.assertFalse(buffer.hasRemaining());
        buffer.rewind();
        Assert.assertEquals(buffer.getInt(), position);
        // now change the address manager to handle longs instead (still valid address to decode)
        manager = new FileAddressManager(Long.BYTES);
        buffer = manager.encodeAddress(address, ByteBuffer.allocate(manager.getAddressSize()));
        buffer.rewind();
        Assert.assertEquals(buffer.getLong(), 10);
    }


    @DataProvider
    public Object[][] illegalEncodeArguments() {
        final FileAddressManager manager = new FileAddressManager(4);
        return new Object[][] {
                // null arguments
                {manager, manager.getUndefinedAddress(), null},
                {manager, null, ByteBuffer.allocate(5)},
                // small buffer
                {manager, manager.getUndefinedAddress(), ByteBuffer.allocate(1)}
        };
    }

    @Test(dataProvider = "illegalEncodeArguments", expectedExceptions = IllegalArgumentException.class)
    public void testIllegalEncodeArguments(final FileAddressManager manager,
            final FileAddress address, final ByteBuffer buffer) throws Exception {
        manager.encodeAddress(address, buffer);
    }

    @DataProvider
    public Object[][] illegalDecodeArguments() {
        final FileAddressManager manager = new FileAddressManager(4);
        return new Object[][] {
                // null arguments
                {manager, null},
                // small buffer
                {manager, ByteBuffer.allocate(1)}
        };
    }

    @Test(dataProvider = "illegalDecodeArguments", expectedExceptions = IllegalArgumentException.class)
    public void testIllegalDecodeArguments(final FileAddressManager manager,
            final ByteBuffer buffer) throws Exception {
        manager.decodeAddress(buffer);
    }

    @Test
    public void testPaddedFileAddress() throws Exception {
        // integer manager
        final FileAddressManager manager = new FileAddressManager(4);

        // an integer encoded with 5 bytes, but it is padded by 0
        final FileAddress intAddress = new FileAddress(new byte[] {0, 1, -1, 1, 1});

        // int encoded with 5 bytes fits into the manager
        final ByteBuffer buffer = (ByteBuffer) manager
                .encodeAddress(intAddress, ByteBuffer.allocate(4)).rewind();
        // and the int value should be the same as the position
        Assert.assertEquals(buffer.getInt(), intAddress.position);

        // a long encoded with only 5 bytes, because the rest are not important
        // this do not fits into an int manager, so it should throw
        final FileAddress longAddress = new FileAddress(new byte[] {1, 1, -1, 1, 1});
        Assert.assertThrows(HDF5jException.FileAddressException.class,
                () -> manager.encodeAddress(longAddress, ByteBuffer.allocate(4)));

        // an undefined address can always be encoded, without throwing
        final FileAddress undefined = new FileAddress(new byte[] {-1, -1, -1, -1, -1});
        final ByteBuffer undefBuffer = (ByteBuffer) manager
                .encodeAddress(undefined, ByteBuffer.allocate(4)).rewind();
        Assert.assertEquals(undefBuffer.getInt(), -1);
    }

}