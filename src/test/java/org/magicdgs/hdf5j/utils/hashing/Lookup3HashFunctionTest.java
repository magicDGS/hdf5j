package org.magicdgs.hdf5j.utils.hashing;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class Lookup3HashFunctionTest {

    @DataProvider
    public static Object[][] bytesToHash() {
        // create large data for the test
        // large array
        final byte[] large = new byte[3093];
        for (int i = 0; i < large.length; i++) {
            large[i] = (byte) (i * 3);
        }
        final int largeHashZeroes = 0x930c7afc;
        final int largeHash = 0x1bd2ee7b;

        return new Object[][] {
                // empty byte array (internal state constant)
                {new byte[0], 0, 0, 0xdeadbeef},
                // ZEROES
                // index only one byte
                {new byte[1], 0, 1, 0x8ba9414b},
                // index only one byte for a longer array (different indexes)
                {new byte[2], 0, 1, 0x8ba9414b},
                {new byte[2], 1, 2, 0x8ba9414b},
                // from 2 to 12 zeroes, to cover all switch cases
                {new byte[2], 0, 2, 0x62cd61b3},
                {new byte[3], 0, 3, 0x6bd0060f},
                {new byte[4], 0, 4, 0x049396b8},
                {new byte[5], 0, 5, 0xd9a91087},
                {new byte[6], 0, 6, 0x23700e53},
                {new byte[7], 0, 7, 0x7dc54d65},
                {new byte[8], 0, 8, 0x58c184bf},
                {new byte[9], 0, 9, 0x0ee58a0d},
                {new byte[10], 0, 10, 0xc343af0b},
                {new byte[11], 0, 11, 0x6e0964a9},
                {new byte[12], 0, 12, 0x1b68e557},
                // large with zeroes
                {new byte[large.length], 0, large.length, largeHashZeroes},

                // VALUES
                // one value
                {new byte[] {23}, 0, 1, 0xa209c931},
                //two values (one, unsigned byte)
                {new byte[] {23, (byte) 187}, 0, 2, 0x8ba7a6c9},
                // three values
                {new byte[] {23, (byte) 187, 98}, 0, 3, 0xcebdf4f0},
                // four values
                {new byte[] {23, (byte) 187, 98, (byte) 217}, 0, 4, 0x2c88bb51},
                // large with values
                {large, 0, large.length, largeHash}
        };
    }

    @Test(dataProvider = "bytesToHash")
    public void testHashBytes(byte[] input, int k, int len, final int expectedHash)
            throws Exception {
        Assert.assertEquals(Lookup3HashFunction.hashBytes(input, k, len, 0), expectedHash);
    }
}