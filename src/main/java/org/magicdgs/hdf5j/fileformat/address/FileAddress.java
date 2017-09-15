package org.magicdgs.hdf5j.fileformat.address;

import org.magicdgs.hdf5j.utils.HDF5jException;

import com.google.common.base.Preconditions;

import java.math.BigInteger;

/**
 * Representation of a file address (position to seek). To construct a {@link FileAddress}, use
 * {@link FileAddressManager}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote this class is a wrapper around a {@code byte[]} that represents a big-endian number,
 * which is cached as a positive {@code long} to keep the byte array.
 * @see FileAddressManager
 */
public final class FileAddress {

    // bytes in big-endian
    final byte[] bytes;
    // cached long for seek; -1 is undefined
    final long position;

    /**
     * Constructs a FileAddress from the provided bytes (big-endian).
     *
     * @param bytes big-endian bytes representing a position in the file.
     *
     * @throws HDF5jException.FileAddressException if there is a problem getting on the address
     *                                             parsing.
     */
    FileAddress(final byte[] bytes) {
        this.bytes = bytes;
        this.position = getLongValue(bytes);
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
                    throw new HDF5jException.FileAddressException(String
                            .format("%s : Position %s cannot be converted to long",
                                    baseHexDisplay(bytes.length, bigInteger.toString(16)),
                                    bigInteger));
                }
            }
        }
        // -1 is the indication of an undefined address
        return -1;
    }

    /**
     * Compares if two addresses point to the same position. Note that this is independent of the
     * number of bytes used for encoding the address.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileAddress)) {
            return false;
        }

        // using the position to compare addresses instead of the bytes
        // this will allow to change the number of bytes to encode an address
        return position == ((FileAddress) o).position;
    }

    @Override
    public int hashCode() {
        // using the position as in equals
        return Long.hashCode(position);
    }

    /**
     * Returns the hexadecimal representation of the address, indicating the number of bytes used to
     * encode them.
     *
     * @return address hexadecimal representation.
     */
    public String hexDisplay() {
        return (position == -1 ? "Undefined" : "File") + baseHexDisplay(bytes.length, Long.toHexString(position));
    }

    // helper method to display both hexadecimal addresses and errors while using BigInteger
    private static final String baseHexDisplay(final int numberOfBytes, final String hexString) {
        return String.format("Address[%s]:0x%s", numberOfBytes, hexString);
    }

    @Override
    public String toString() {
        return hexDisplay();
    }
}