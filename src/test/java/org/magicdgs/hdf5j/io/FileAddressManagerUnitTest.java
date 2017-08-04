package org.magicdgs.hdf5j.io;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressManagerUnitTest {

    @DataProvider
    public Object[][] fileAddresses() {

        final BigInteger maxIntPlusOne = BigInteger
                .valueOf(Integer.MAX_VALUE)
                .add(BigInteger.valueOf(1));

        // data: byte-buffer, expected length, expected position
        return new Object[][] {
                // maximum integer
                {ByteBuffer.allocate(Integer.BYTES).putInt(Integer.MAX_VALUE), Integer.BYTES, Integer.MAX_VALUE},
                // unsigned int
                {ByteBuffer.allocate(Integer.BYTES).putInt(Integer.MIN_VALUE), Integer.BYTES, maxIntPlusOne},
                // some values
                {ByteBuffer.allocate(Integer.BYTES).putInt(10), Integer.BYTES, 10},
                {ByteBuffer.allocate(Long.BYTES).putLong(1000000000000000L), Long.BYTES, 1000000000000000L},
                // max int + 1 (as long)
                {ByteBuffer.allocate(Long.BYTES).putLong(maxIntPlusOne.longValue()), Long.BYTES, maxIntPlusOne.longValueExact()},
                // get the bytes from a big integer
                {ByteBuffer.wrap(maxIntPlusOne.toByteArray()), 1 + maxIntPlusOne.bitLength() / 8, maxIntPlusOne},
                // maximum long
                {ByteBuffer.allocate(Long.BYTES).putLong(Long.MAX_VALUE), Long.BYTES, Long.MAX_VALUE},


        };
    }

    @Test(dataProvider = "fileAddresses")
    public void testFileAddressFromNumbers(final ByteBuffer bytes, final int expectedLength, final Number expectedPosition) throws Exception {
        final FileAddressManager.FileAddress address = new FileAddressManager.FileAddress(bytes.array());
        System.err.println(address);
        Assert.assertEquals(address.bytes.length, expectedLength);
        Assert.assertEquals(address.position, expectedPosition.longValue());
    }

    @Test
    public void testFileAddressManager() {
        final FileAddressManager manager = new FileAddressManager(10);
        Assert.assertEquals(manager.getAddressSize(), 10);
        Assert.assertNotNull(manager.getUndefinedAddress());
    }


}