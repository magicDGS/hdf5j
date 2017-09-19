package org.magicdgs.hdf5j.fileformat;

import org.magicdgs.hdf5j.HDF5jTest;
import org.magicdgs.hdf5j.utils.exceptions.FileAddressException;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressUnitTest extends HDF5jTest {

    @DataProvider
    public Object[][] hexDisplayData() {
        return new Object[][] {
                // 1-byte encoding (edge case)
                {new byte[] {-1}, "FileAddress[0xff]"},
                {new byte[] {0}, "FileAddress[0x00]"},
                {new byte[] {Byte.MAX_VALUE}, "FileAddress[0x7f]"},
                // integer encoding (8-bits)
                {ByteBuffer.allocate(Integer.BYTES).putInt(-1).array(),
                        "FileAddress[0x" + Integer.toHexString(-1) + "]"},
                {ByteBuffer.allocate(Integer.BYTES).putInt(Integer.MAX_VALUE).array(),
                        "FileAddress[0x" + Integer.toHexString(Integer.MAX_VALUE) + "]"},
                // long encoding (maximum bits in our framework)
                {ByteBuffer.allocate(Long.BYTES).putLong(-1).array(),
                        "FileAddress[0x" + Long.toHexString(-1) + "]"},
                {ByteBuffer.allocate(Long.BYTES).putLong(Long.MAX_VALUE).array(),
                        "FileAddress[0x" + Long.toHexString(Long.MAX_VALUE) + "]"}
        };
    }

    @Test(dataProvider = "hexDisplayData")
    public void testHexDisplay(final byte[] addressBytes, final String expected) {
        // test the static method
        Assert.assertEquals(FileAddress.hexDisplay(addressBytes), expected);
        // test the default implementation (mock class)
        final FileAddress address = Mockito.mock(FileAddress.class);
        Mockito.when(address.hexDisplay()).thenCallRealMethod();
        // using the address bytes
        Mockito.when(address.asByteArray()).thenReturn(addressBytes);
        Assert.assertEquals(address.hexDisplay(), expected);
    }

    @Test
    public void testIsUndefined() {
        // mock the address and call the default implementation for isUndefined()
        final FileAddress address = Mockito.mock(FileAddress.class);
        Mockito.when(address.isUndefinded()).thenCallRealMethod();

        // mock the return value of getFilePointer() to be undefined
        Mockito.when(address.getFilePointer()).thenReturn(-1L);
        Assert.assertTrue(address.isUndefinded());
        // mock the return value of getFilePointer() to be a proper value
        Mockito.when(address.getFilePointer()).thenReturn(10L);
        Assert.assertFalse(address.isUndefinded());
    }

    @Test
    public void testSeek() throws Exception {
        // mock the address and call the default implementation for seek
        final FileAddress address = Mockito.mock(FileAddress.class);
        Mockito.when(address.seek(Mockito.any())).thenCallRealMethod();
        // test with null byte channel
        Assert.assertThrows(IllegalArgumentException.class, () -> address.seek(null));

        // test with mocked file channel
        final SeekableByteChannel channel = Mockito.mock(SeekableByteChannel.class);
        final long position = 10;
        Mockito.when(channel.position(position)).thenReturn(channel);
        // file address should return positive integer
        Mockito.when(address.getFilePointer()).thenReturn(position);
        Assert.assertSame(address.seek(channel), channel);

        // test with the mocked classed being undefined
        Mockito.when(address.isUndefinded()).thenReturn(true);
        Assert.assertThrows(FileAddressException.class,
                () -> address.seek(Mockito.mock(SeekableByteChannel.class)));
    }

}