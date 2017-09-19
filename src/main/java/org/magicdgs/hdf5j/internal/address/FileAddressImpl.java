package org.magicdgs.hdf5j.internal.address;

import org.magicdgs.hdf5j.fileformat.FileAddress;
import org.magicdgs.hdf5j.utils.exceptions.FileAddressException;

import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Internal implementation of {@link FileAddress}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote this class is a wrapper around a {@code byte[]} that represents a big-endian number,
 * which is cached as a positive {@code long} to keep the byte array.
 * @see FileAddressManager
 */
public final class FileAddressImpl implements FileAddress {

    // bytes in big-endian
    private byte[] bytes;
    // cached long for seek; -1 is undefined
    private final long position;

    /**
     * Private constructor, which do not check if the bytes and position are in sync.
     * @param bytes big-endian bytes representing a position in the file.
     * @param position position representation.
     */
    private FileAddressImpl(final byte[] bytes, final long position) {
        this.bytes = bytes;
        this.position = position;
    }

    /**
     * Constructs a {@link FileAddress} from the provided bytes (big-endian).
     *
     * <p>To construct a {@link FileAddressImpl}, {@link FileAddressManager} should be used.
     *
     * @param bytes big-endian bytes representing a position in the file.
     *
     * @throws FileAddressException if there is a problem getting on the address
     *                              parsing.
     */
    FileAddressImpl(final byte[] bytes) {
        this(bytes, getLongValue(bytes));
    }

    /**
     * Gets the undefined address for a concrete number of bytes. The undefined address is the
     * one with all the bytes unset.
     *
     * @param size the number of bytes that the address should have.
     *
     * @return new undefined address.
     */
    public static FileAddressImpl getUndefinedAddressForSize(final int size) {
        final byte[] undefinedBytes = new byte[size];
        Arrays.fill(undefinedBytes, (byte) -1);
        // use the private constructor to avoid iterating over all undefined bytes
        return new FileAddressImpl(undefinedBytes, -1);
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
                    throw new FileAddressException(
                            "Position " + bigInteger + " cannot be converted to long");
                }
            }
        }
        return UNDEFINED_FILE_POINTER;
    }

    @Override
    public long getFilePointer() {
        return position;
    }

    @Override
    public int getNumberOfBytes() {
        return bytes.length;
    }

    @Override
    public byte[] asByteArray() {
        return bytes;
    }

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
        return this.getFilePointer() == ((FileAddress) o).getFilePointer();
    }

    @Override
    public int hashCode() {
        // using the position as in equals
        return Long.hashCode(position);
    }

    @Override
    public String toString() {
        return hexDisplay();
    }
}