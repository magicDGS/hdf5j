package org.magicdgs.hdf5j.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import sun.jvm.hotspot.debugger.AddressException;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Manager for file addresses encoded in an arbitrary number of bytes. The manager provides:
 *
 * <ul>
 *     <li>Factory methods to create addresses based on an address size</li>
 *     <li>Decoder/Encoder methods</li>
 *     <li>Validation methods </li>
 * </ul>
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressManager {

    private final int addressSize;

    // cached undefinedAddress for the size in the factory
    private final FileAddress undefinedAddress;

    public FileAddressManager(int addressSize) {
        this.addressSize = addressSize;
        // TODO: do not cache?
        undefinedAddress = getUndefined(addressSize);
        Preconditions.checkArgument(undefinedAddress.bytes.length == addressSize, undefinedAddress.bytes.length);
    }

    // TODO: initialize undefinedAddress (all bits set)
    private static final FileAddress getUndefined(final int size) {
        final int bitSize = size * Byte.SIZE;
        final BitSet bits = new BitSet(bitSize);
        for (int i = 0; i < bitSize; i++) {
            bits.set(i);
        }
        return new FileAddress(bits.toByteArray());
    }

    /**
     * Get the size of the addresses decoded/encoded by this file address manager.
     */
    public int getAddressSize() {
        return addressSize;
    }

    /**
     * Gets the undefined address for this
     * @return
     */
    public FileAddress getUndefinedAddress() {
        return undefinedAddress;
    }

    public boolean isUndefined(final FileAddress address) {
        return undefinedAddress.equals(address);
    }

    /**
     * Reads an address from the DataInput
     */
    public FileAddress decodeAddress(final DataInput dataInput) throws IOException {
        Preconditions.checkArgument(dataInput != null, "null dataInput");
        final byte[] bytes = new byte[addressSize];
        dataInput.readFully(bytes);
        return new FileAddress(bytes);
    }

    /**
     * Checks if the address provided is correctly sized with respect to the factory.
     *
     * @param address the address to check.
     *
     * @return the same file address.
     * @throws IllegalArgumentException if the address is not valid for this factory.
     * TODO: use custom exception
     */
    public FileAddress validateAddress(final FileAddress address) {
        Preconditions.checkArgument(address != null, "null address");
        Preconditions.checkArgument(address.bytes.length <= getAddressSize(), "address is encoded with more bytes than the address manager");
        return address;
    }

    /**
     * Seeks the byte chanel to the provided file address.
     */
    public void seek(final SeekableByteChannel byteChannel, final FileAddress address)
            throws IOException {
        byteChannel.position(address.position);
    }

    /**
     * Wrapper around a byte array with a custom number of bytes to store file addresses to seek
     * from
     * files.
     *
     * @author Daniel Gomez-Sanchez (magicDGS)
     */
    public static class FileAddress {

        // bytes in big-endian
        @VisibleForTesting
        final byte[] bytes;
        // cached long for seek
        @VisibleForTesting
        final long position;

        /**
         * Constructs a FileAddress from the provided bytes (big-endian).
         *
         * @param bytes big-endian bytes representing a position in the file.
         */
        @VisibleForTesting
        FileAddress(final byte[] bytes) {
            // TODO: maybe be safe and copy the bytes
            this.bytes = bytes;
            // TODO: we should check that
            // use the exact long value (will blow up with ArithmeticException if it produces overflow)
            // TODO: wrap the ArithmeticException into a custom exception to be more informative
            this.position = new BigInteger(1, bytes).longValue();
            // TODO: we should also check if it is negative -> even if it shouldn't!
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FileAddress)) {
                return false;
            }

            // both bytes and BigInteger should represent the same
            // use only the bytes because they are more exhaustive in a byte-by-byte comparison
            // TODO: we should use the long value (because it is independent of the number of bytes used for encoding)!
            return Arrays.equals(bytes, ((FileAddress) o).bytes);
        }

        @Override
        public int hashCode() {
            // both bytes and BigInteger should represent the same
            // use BigInteger because it is more efficient
            return Long.hashCode(position);
        }

        /** Returns the hexadecimal representation of the address. */
        @Override
        public String toString() {
            return String.format("FileAddress:%sx%s", bytes.length, Long.toHexString(position));
        }
    }

}
