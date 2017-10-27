package org.magicdgs.hdf5j.fileformat;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;

/**
 * Representation of a object size (number of bytes to read/write from an object).
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface ObjectSize {

    /** Value for {@link #getObjectLength()} for the unlimited object size. */
    public static final long UNLIMITED_OBJECT_LENGTH = -1;

    /** Simple implementation of unlimited object size. */
    public static final ObjectSize UNLIMITED_SIZE = new ObjectSize() {

        /** Returns {@link #UNLIMITED_OBJECT_LENGTH}. */
        @Override
        public long getObjectLength() {
            return UNLIMITED_OBJECT_LENGTH;
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
            if (!(obj instanceof ObjectSize)) {
                return false;
            }

            return this.getObjectLength() == ((ObjectSize) obj).getObjectLength();
        }

        @Override
        public int hashCode() {
            return Long.hashCode(getObjectLength());
        }
    };

    /**
     * Gets the object size.
     *
     * <p>This is the long representation for the object size used for read/write an object. For
     * example, it may be use for allocating a {@link ByteBuffer} and read an object using
     * {@link SeekableByteChannel#read(ByteBuffer)}.
     *
     * @return object size represented by this instance (strictly positive);
     * {@link #UNLIMITED_OBJECT_LENGTH} if unlimited.
     */
    public long getObjectLength();

    /**
     * Checks if this file represents an undefined address.
     *
     * <p>Default implementation checks if {@link #getObjectLength()} is equals to
     * {@link #UNLIMITED_OBJECT_LENGTH}.
     *
     * @return {@code true} if the address is undefined; {@code false} otherwise.
     */
    public default boolean isUnlimited() {
        return getObjectLength() == UNLIMITED_OBJECT_LENGTH;
    }

    /**
     * Gets the byte array representation for this object size, in big-endian order.
     *
     * <p><b>WARNING</b>: implementations of this method are not forced to return an unmodifiable
     * byte array. Do not modify the returned array unless sub-classes specify that it is safe.
     *
     * @return byte array in big-endian order to encode the object size.
     *
     * @throws org.magicdgs.hdf5j.utils.exceptions.ObjectSizeException if the object size cannot be
     *                                                                 encoded with this number of
     *                                                                 bytes.
     */
    public byte[] asByteArray(final int numberOfBytes);

    /**
     * Returns {@code true} if the two object sizes represent the same objecte length; {@code false}
     * otherwise.
     *
     * @param obj the reference object with which to compare.
     *
     * @return {@code true} if the objects are equal; {@code false} otherwise.
     *
     * @implNote this method should compare only the value returned by {@link #getObjectLength()}
     * and work for any {@link ObjectSize} implementation to make them interchangeable.
     */
    @Override
    public boolean equals(final Object obj);

    /**
     * {@inheritDoc}
     *
     * @implNote this method should include only the value returned by {@link #getObjectLength()}.
     * It is recommended to use {@link Long#hashCode(long)} for object size.
     */
    @Override
    public int hashCode();
}
