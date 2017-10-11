package org.magicdgs.hdf5j.fileformat;

import org.magicdgs.hdf5j.utils.exceptions.FileAddressException;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import java.util.Objects;

/**
 * Representation of a file address (position to seek).
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface FileAddress {

    /** Value for {@link #getFilePointer()} for the undefined address. */
    public static final long UNDEFINED_FILE_POINTER = -1;

    /** Simple implementation of undefined address. */
    public static final FileAddress UNDEFINED_ADDRESS = new FileAddress() {

        /** Returns {@link #UNDEFINED_FILE_POINTER}. */
        @Override
        public long getFilePointer() {
            return UNDEFINED_FILE_POINTER;
        }

        /** Returns a fresh byte[] where every byte is set to -1. */
        @Override
        public byte[] asByteArray(int numberOfBytes) {
            final byte[] undefinedBytes = new byte[numberOfBytes];
            Arrays.fill(undefinedBytes, (byte) -1);
            return undefinedBytes;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FileAddress)) {
                return false;
            }

            return  this.getFilePointer() == ((FileAddress) obj).getFilePointer();
        }

        @Override
        public int hashCode() {
            return Long.hashCode(getFilePointer());
        }
    };

    /**
     * Gets the file pointer for this address.
     *
     * <p>This is the long representation for the file address to be used for seek a file. For
     * example, using {@link SeekableByteChannel#position(long)}.
     *
     * @return file position represented by this address (strictly positive);
     * {@link #UNDEFINED_FILE_POINTER} if undefined.
     */
    public long getFilePointer();

    /**
     * Gets the byte array representation for this address, in big-endian order,
     *
     * <p><b>WARNING</b>: implementations of this method are not forced to return an unmodifiable
     * byte array. Do not modify the returned array unless sub-classes specify that it is safe.
     *
     * @return byte array in big-endian order to encode the address.
     * @throws org.magicdgs.hdf5j.utils.exceptions.FileAddressException if the address cannot be encoded with this number of bytes.
     */
    public byte[] asByteArray(final int numberOfBytes);

    /**
     * Returns the hexadecimal representation of the address from the byte array.
     *
     * <p>Default implementation calls {@code hexDisplay(asByteArray(Long.BYTES))}, but this could
     * throw for implementations caching the byte array.
     *
     * @return address hexadecimal representation.
     * @implNote it is recommended to use {@link #hexDisplay(byte[])} for a common representation
     * of the addresses in an implementation-independent manner.
     */
    public default String hexDisplay() {
        return hexDisplay(asByteArray(Long.BYTES));
    }

    /**
     * Checks if this file represents an undefined address.
     *
     * <p>Default implementation checks if {@link #getFilePointer()} is equals to
     * {@link #UNDEFINED_FILE_POINTER}.
     *
     * @return {@code true} if the address is undefined; {@code false} otherwise.
     */
    public default boolean isUndefinded() {
        return getFilePointer() == UNDEFINED_FILE_POINTER;
    }

    /**
     * Seeks the byte chanel to the file pointer position.
     *
     *
     * <p>Default implementation returns the byte channel after calling {@link
     * SeekableByteChannel#position(long)} if the {@link FileAddress} is not undefined.
     *
     * @param channel non-null channel to seek.
     *
     * @return the byte channel after position in the pointer from this address.
     *
     * @throws FileAddressException if the address is undefined.
     * @throws IOException          if an I/O error occurs when position the byte channel.
     */
    public default SeekableByteChannel seek(final SeekableByteChannel channel) throws IOException {
        Preconditions.checkArgument(channel != null, "cannot seek null channel");
        if (isUndefinded()) {
            throw new FileAddressException(this, "impossible to seek an undefined address");
        }
        return channel.position(getFilePointer());
    }

    /**
     * Displays the hexadecimal representation for a file address.
     *
     * @param addressBytes bytes of the address (as returned by {@link #asByteArray(int)}.
     *
     * @return address in hexadecimal representation.
     */
    public static String hexDisplay(final byte[] addressBytes) {
        final StringBuilder builder = new StringBuilder("FileAddress[0x");
        for (final byte b : addressBytes) {
            // hexadecimal format, padding with 0
            builder.append(String.format("%02x", b));
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Returns {@code true} if the two addresses point to the same position in the file; {@code
     * false} otherwise.
     *
     * @param obj the reference object with which to compare.
     *
     * @return {@code true} if the objects are equal; {@code false} otherwise.
     *
     * @implNote this method should compare only the value returned by {@link #getFilePointer()} and
     * work for any {@link FileAddress} implementation to make them interchangeable.
     */
    @Override
    public boolean equals(final Object obj);

    /**
     * {@inheritDoc}
     *
     * @implNote this method should include only the value returned by {@link #getFilePointer()}.
     * It is recommended to use {@link Long#hashCode(long)} for the file pointer.
     */
    @Override
    public int hashCode();

}
