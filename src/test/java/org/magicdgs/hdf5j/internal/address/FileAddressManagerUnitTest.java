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
public class FileAddressManagerUnitTest extends HDF5jTest {

    @DataProvider
    public Object[][] undefineAddressSizes() {
        return new Object[][] {{1}, {5}, {10}, {20}};
    }

    @Test(dataProvider = "undefineAddressSizes")
    public void testUndefinedAddress(final int size) throws Exception {
        FileAddressManager manager = new FileAddressManager(size);
        Assert.assertEquals(manager.getUndefinedAddress().getNumberOfBytes(), size);
        Assert.assertEquals(manager.getUndefinedAddress().getFilePointer(), -1);
    }

    @DataProvider
    public Object[][] buffersToDecode() {
        final int position = 189765;
        return new Object[][] {
                // try with int and long
                {Integer.BYTES, ByteBuffer.allocate(Integer.BYTES).putInt(position), position},
                {Long.BYTES, ByteBuffer.allocate(Long.BYTES).putLong(position), position},
                // undefined position
                {Integer.BYTES, ByteBuffer.wrap(new byte[] {-1, -1, -1, -1}), -1},
                {Long.BYTES, ByteBuffer.wrap(new byte[] {-1, -1, -1, -1, -1, -1, -1, -1}), -1}
        };
    }

    @Test(dataProvider = "buffersToDecode")
    public void testDecodeAddressFromBuffer(final int size, final ByteBuffer buffer,
            final long position) {
        // rewinds the buffer to read from the beginning
        buffer.rewind();
        final FileAddressManager manager = new FileAddressManager(size);
        final FileAddress address = manager.decodeAddress(buffer);
        Assert.assertEquals(address.getNumberOfBytes(), size);
        Assert.assertEquals(address.getFilePointer(), position);
        // check that the buffer is consumed
        Assert.assertFalse(buffer.hasRemaining());
    }

    @DataProvider
    public Object[][] longsToDecode() {
        return new Object[][] {
                // try with int and long
                {Integer.BYTES, 189765},
                {Long.BYTES, 189765},
                {Long.BYTES, Long.MAX_VALUE},
                // undefined position
                {Integer.BYTES, -1},
                {Long.BYTES, -1}
        };
    }

    @Test(dataProvider = "longsToDecode")
    public void testDecodeAddressFromPosition(final int size, final long position)
            throws Exception {
        final FileAddressManager manager = new FileAddressManager(size);
        final FileAddress address = manager.decodeAddress(position);
        Assert.assertEquals(address.getNumberOfBytes(), size);
        Assert.assertEquals(address.getFilePointer(), position);
    }

    @Test
    public void testEncodeAddress() throws Exception {
        final int position = 10;
        // allocate a buffer with long
        final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        // encode first as a int
        FileAddressManager manager = new FileAddressManager(Integer.BYTES);
        final FileAddress address = new FileAddressImpl(
                ByteBuffer.allocate(manager.getAddressSize()).putInt(position).array());
        manager.encodeAddress(address, buffer);
        buffer.flip();
        Assert.assertEquals(buffer.getInt(), position);
        // now change the address manager to handle longs instead (still valid address to decode)
        manager = new FileAddressManager(Long.BYTES);
        // read in a reset buffer
        manager.encodeAddress(address, (ByteBuffer) buffer.clear());
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
    public void testIllegalEncodeAddress(final FileAddressManager manager,
            final FileAddressImpl address, final ByteBuffer buffer) throws Exception {
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
    public void testIllegalDecodeAddressFromBuffer(final FileAddressManager manager,
            final ByteBuffer buffer) throws Exception {
        manager.decodeAddress(buffer);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testIllegalDecodeAddressFromLong() throws Exception {
        new FileAddressManager(4).decodeAddress(-2);
    }

    @Test
    public void testNormalizeAddress() throws Exception {
        final FileAddressManager manager = new FileAddressManager(4);

        // an undefined address can always be encoded, without throwing
        final FileAddress undefined = new FileAddressImpl(new byte[] {-1, -1, -1, -1, -1});
        // an integer encoded with 4 bytes (the same size)
        final FileAddress intEncoded = new FileAddressImpl(new byte[] {0, 1, 1, 1});
        // an integer encoded with 5 bytes, but it is padded by 0
        // TODO: should we really accept this?
        final FileAddress intAddress = new FileAddressImpl(new byte[] {0, 1, -1, 1, 1});
        // a long encoded with only 5 bytes, because the rest are not important
        // this do not fits into an int manager, so it should throw
        final FileAddress longAddress = new FileAddressImpl(new byte[] {1, 1, -1, 1, 1});

        // returns the cached undefined (same object)
        Assert.assertSame(manager.normalizeAddress(undefined), manager.getUndefinedAddress());
        // returns the same address if it is already properly encoded
        Assert.assertSame(manager.normalizeAddress(intEncoded), intEncoded);

        // returns not the same object, but equals
        Assert.assertNotSame(manager.normalizeAddress(intAddress), intAddress);
        Assert.assertEquals(manager.normalizeAddress(intAddress), intAddress);
        // this should throw
        Assert.assertThrows(FileAddressException.class,
                () -> manager.normalizeAddress(longAddress));
    }

}