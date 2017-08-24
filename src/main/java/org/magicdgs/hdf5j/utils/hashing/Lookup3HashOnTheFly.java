package org.magicdgs.hdf5j.utils.hashing;

import com.google.common.base.Preconditions;

/**
 * TODO: trial to allow on the fly hashing with the lookup3 hash algorithm without loading all
 * TODO: the data on memory
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class Lookup3HashOnTheFly {

    private static final int INTERNAL_STATE_CONSTANT = 0xdeadbeef;
    private static final int BLOCK_LENGTH = 12;

    private int length;

    // because the numbers are going one by one
    private final int[] buffer = new int[BLOCK_LENGTH];
    private int bufferPosition = 0;

    private int a;
    private int b;
    private int c;

    public Lookup3HashOnTheFly(final int length, final int initValue) {
        Preconditions.checkArgument(length > 0, "negative length");
        this.length = length;
        this.a = INTERNAL_STATE_CONSTANT + length + initValue;
        this.b = a;
        this.c = a;
    }

    // TODO: javadoc
    public void update(byte newByte) {
        Preconditions.checkState(length > 0, "Cannot add more bytes to the hasher");
        // TODO: blow up if the length is already passed
        if (bufferPosition == buffer.length && length > BLOCK_LENGTH) {
            noLastBlockComputation();
            bufferPosition = 0;
        }
        buffer[bufferPosition] = Byte.toUnsignedInt(newByte);
        bufferPosition++;
        length--;
    }

    public int finalizeHash() {
        // TODO: this should check if it is already computed....
        Preconditions.checkState(length == 0, "Cannot finalize hash: require %s more bytes", length);
        switch (bufferPosition) {
            case 12:
                c += buffer[11] << 24;
            case 11:
                c += buffer[10] << 16;
            case 10:
                c += buffer[9] << 8;
            case 9:
                c += buffer[8];
            case 8:
                b += buffer[7] << 24;
            case 7:
                b += buffer[6] << 16;
            case 6:
                b += buffer[5] << 8;
            case 5:
                b += buffer[4];
            case 4:
                a += buffer[3] << 24;
            case 3:
                a += buffer[2] << 16;
            case 2:
                a += buffer[1] << 8;
            case 1:
                a += buffer[0];
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

    private void noLastBlockComputation() {
        a += buffer[0];
        a += buffer[1] << 8;
        a += buffer[2] << 16;
        a += buffer[3] << 24;
        b += buffer[4];
        b += buffer[5] << 8;
        b += buffer[6] << 16;
        b += buffer[7] << 24;
        c += buffer[8];
        c += buffer[9] << 8;
        c += buffer[10] << 16;
        c += buffer[11] << 24;

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
    }

    // rotate function
    private static int rot(int x, int k) {
        return (x << k) | (x >>> (32 - k));
    }

}
