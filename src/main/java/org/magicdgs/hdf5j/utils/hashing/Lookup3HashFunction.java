package org.magicdgs.hdf5j.utils.hashing;

import com.google.common.base.Preconditions;

/**
 * Implementation of <a href="http://www.burtleburtle.net/bob/hash/doobs.html">
 * Bob Jenkins' algorithm for 32-bits hashing</a>. Concretely, it implements the
 * <a href="http://www.burtleburtle.net/bob/c/lookup3.c">{@code lookup3.c#hashlittle()}</a>
 * function.
 * <p>
 * Use for hash table lookup, or anything where one collision in 2^^32 is acceptable. Do NOT use
 * for cryptographic purposes.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class Lookup3HashFunction {

    private static final int INTERNAL_STATE_CONSTANT = 0xdeadbeef;
    private static final int LAST_BLOCK_LENGTH = 12;

    /**
     * Hashes a variable-length key into a 32-bit value using the
     * <a href="http://www.burtleburtle.net/bob/c/lookup3.c">{@code lookup3.c#hashlittle()}</a>
     * function by <a href="http://www.burtleburtle.net/bob/hash/doobs.html">Bob Jenkins
     * (2006)</a>.
     * <p>
     * Every bit of the key affects every bit of the return value. Two keys differing by one or two
     * bits will have totally different hash values.
     * <p>
     * Use for hash table lookup, or anything where one collision in 2^^32 is acceptable. Do NOT use
     * for cryptographic purposes.
     *
     * @param input     unaligned variable-length array of bytes.
     * @param offset    start hashing from this index.
     * @param length    number of bytes to include.
     * @param initValue value for initialize the hash.
     *
     * @return a 32-bit hash.
     */
    public final static int hashBytes(final byte[] input, final int offset, final int length,
            final int initValue) {
        Preconditions.checkPositionIndexes(offset, length, input.length);

        // compute the lenght that it is going to be used in the hash
        int len = length - offset;

        // setting up the internal state
        int a = INTERNAL_STATE_CONSTANT + len + initValue;
        int b = a;
        int c = a;

        int off = offset;
        // all but the last block
        while (len > LAST_BLOCK_LENGTH) {
            a += Byte.toUnsignedInt(input[off]);
            a += Byte.toUnsignedInt(input[off + 1]) << 8;
            a += Byte.toUnsignedInt(input[off + 2]) << 16;
            a += Byte.toUnsignedInt(input[off + 3]) << 24;
            b += Byte.toUnsignedInt(input[off + 4]);
            b += Byte.toUnsignedInt(input[off + 5]) << 8;
            b += Byte.toUnsignedInt(input[off + 6]) << 16;
            b += Byte.toUnsignedInt(input[off + 7]) << 24;
            c += Byte.toUnsignedInt(input[off + 8]);
            c += Byte.toUnsignedInt(input[off + 9]) << 8;
            c += Byte.toUnsignedInt(input[off + 10]) << 16;
            c += Byte.toUnsignedInt(input[off + 11]) << 24;

            // mixing
            a -= c;
            a ^= rot(c, 4);
            c += b;
            b -= a;
            b ^= rot(a, 6);
            a += c;
            c -= b;
            c ^= rot(b, 8);
            b += a;
            a -= c;
            a ^= rot(c, 16);
            c += b;
            b -= a;
            b ^= rot(a, 19);
            a += c;
            c -= b;
            c ^= rot(b, 4);
            b += a;
            // mixing finished

            len -= LAST_BLOCK_LENGTH;
            off += LAST_BLOCK_LENGTH;
        }

        switch (len) {
            case 12:
                c += Byte.toUnsignedInt(input[off + 11]) << 24;
            case 11:
                c += Byte.toUnsignedInt(input[off + 10]) << 16;
            case 10:
                c += Byte.toUnsignedInt(input[off + 9]) << 8;
            case 9:
                c += Byte.toUnsignedInt(input[off + 8]);
            case 8:
                b += Byte.toUnsignedInt(input[off + 7]) << 24;
            case 7:
                b += Byte.toUnsignedInt(input[off + 6]) << 16;
            case 6:
                b += Byte.toUnsignedInt(input[off + 5]) << 8;
            case 5:
                b += Byte.toUnsignedInt(input[off + 4]);
            case 4:
                a += Byte.toUnsignedInt(input[off + 3]) << 24;
            case 3:
                a += Byte.toUnsignedInt(input[off + 2]) << 16;
            case 2:
                a += Byte.toUnsignedInt(input[off + 1]) << 8;
            case 1:
                a += Byte.toUnsignedInt(input[off]);
                break;
            case 0:
                return c;
            default:
                throw new RuntimeException("Should not reach");
        }

        // final mixing
        c ^= b;
        c -= rot(b, 14);
        a ^= c;
        a -= rot(c, 11);
        b ^= a;
        b -= rot(a, 25);
        c ^= b;
        c -= rot(b, 16);
        a ^= c;
        a -= rot(c, 4);
        b ^= a;
        b -= rot(a, 14);
        c ^= b;
        c -= rot(b, 24);
        // final mixing finished

        return c;
    }

    // rotate function
    private static int rot(final int x, final int k) {
        return (x << k) | (x >>> (32 - k));
    }
}
