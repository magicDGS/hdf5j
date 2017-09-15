package org.magicdgs.hdf5j.fileformat.objects;

import org.magicdgs.hdf5j.utils.HDF5jException;

import com.google.common.base.Preconditions;

import java.math.BigInteger;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class ObjectSize {


    // bytes in big-endian
    final byte[] bytes;
    // cached long for seek; -1 is unlimited
    final long objectSize;

    /**
     * Constructs a ObjectSize from the provided bytes (big-endian).
     *
     * @param bytes big-endian bytes representing a objectSize in the file.
     *
     * @throws HDF5jException if there is a problem converting the object size to long.
     */
    ObjectSize(final byte[] bytes) {
        this.bytes = bytes;
        this.objectSize = getLongValue(bytes);
    }

    // helper method to check if all the bytes are unset
    private final static long getLongValue(final byte[] bytes) {
        Preconditions.checkArgument(bytes != null, "null bytes");
        Preconditions.checkArgument(bytes.length != 0, "empty bytes");

        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != -1) {
                final BigInteger bigInteger = new BigInteger(1, bytes);
                try {
                    return bigInteger.longValueExact();
                } catch (final ArithmeticException e) {
                    // TODO: create new excpetion for this?
                    throw new HDF5jException(bigInteger + " size %s cannot be converted to long");
                }
            }
        }
        // -1 is the indication of an unlimited size
        return -1;
    }

    /**
     * Compares if two addresses point to the same ObjectSize. Note that this is independent of the
     * number of bytes used for encoding the address.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectSize)) {
            return false;
        }

        // using the objectSize to compare addresses instead of the bytes
        // this will allow to change the number of bytes to encode an address
        return objectSize == ((ObjectSize) o).objectSize;
    }

    @Override
    public int hashCode() {
        // using the objectSize as in equals
        return Long.hashCode(objectSize);
    }

    /**
     * Returns a String representation of the ObjectSize.
     *
     * @return object size for display purposes.
     */
    public String displayFormat() {
        return (objectSize == -1 ? "Unlimited" : objectSize) + " bytes";
    }

    @Override
    public String toString() {
        // includes the length of the bytes for debugging purposes
        return String.format("ObjectSize[%s]:%s", bytes.length, displayFormat());
    }

}
