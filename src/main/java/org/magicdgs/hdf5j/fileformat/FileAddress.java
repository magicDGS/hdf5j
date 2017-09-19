package org.magicdgs.hdf5j.fileformat;

import org.magicdgs.hdf5j.utils.exceptions.FileAddressException;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

/**
 * Representation of a file address (position to seek).
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface FileAddress {

    /** Value for {@link #getFilePointer()} for the undefined address. */
    public static final long UNDEFINED_FILE_POINTER = -1;

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
     * Gets the byte array representation for this address, in big-endian order.
     *
     * <p>The length of the array should be the same as the value returned by
     * {@link #getNumberOfBytes()}.
     *
     * <p><b>WARNING</b>: implementations of this method are not forced to return an unmodifiable
     * byte array. Do not modify the returned array unless sub-classes specify that it is safe.
     *
     * @return byte array in big-endian order to encode the address.
     */
    public byte[] asByteArray();


    /**
     * Gets the number of bytes used to encode this file address into a byte array with {@link
     * #asByteArray()}.
     *
     * @return the number of bytes used to encode this file address position.
     */
    public int getNumberOfBytes();

    /**
     * Returns the hexadecimal representation of the address from the byte array.
     *
     * <p>Default implementation calls {@code hexDisplay(asByteArray())}.
     *
     * @return address hexadecimal representation.
     */
    public default String hexDisplay() {
        return hexDisplay(asByteArray());
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
     * @param addressBytes bytes of the address (as returned by {@link #asByteArray()}.
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
     */
    @Override
    public int hashCode();

}
