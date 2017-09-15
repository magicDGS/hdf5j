package org.magicdgs.hdf5j.fileformat.address;

import org.magicdgs.hdf5j.utils.HDF5jException;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.stream.IntStream;

/**
 * Manager for file addresses encoded in an arbitrary number of bytes. The manager provides:
 *
 * <ul>
 * <li>Factory methods to create addresses based on an address size</li>
 * <li>Decoder/Encoder methods</li>
 * <li>Validation methods </li>
 * </ul>
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class FileAddressManager {

    private final int addressSize;
    // cached undefinedAddress for the size in the factory
    private final FileAddress undefinedAddress;

    /**
     * Constructor for a fixed address size.
     *
     * @param addressSize the number of bytes to use to encode the address sizes.
     */
    public FileAddressManager(int addressSize) {
        // get the undefined byte[] array to cache an undefined address
        final byte[] undefinedBytes = new byte[addressSize];
        for (int i = 0; i < addressSize; i++) {
            undefinedBytes[i] = -1;
        }
        this.undefinedAddress = new FileAddress(undefinedBytes);
        this.addressSize = addressSize;
    }

    /**
     * Gets the size of the addresses decoded/encoded by this file address manager.
     *
     * @return size of addresses/offsets.
     */
    public int getAddressSize() {
        return addressSize;
    }

    /**
     * Gets the undefined address for this FileManager.
     *
     * @return cached undefined file address.
     */
    public FileAddress getUndefinedAddress() {
        return undefinedAddress;
    }

    /**
     * Seeks the byte chanel to the provided file address.
     *
     * @param byteChannel channel to seek.
     * @param address     file address to set the pointer to.
     *
     * @return normalized file address representing the current position of the byte channel.
     *
     * @throws HDF5jException.FileAddressException if the address cannot be handled or it is
     *                                             undefined.
     * @throws IOException                         if an IO error occurs.
     * @see #normalizeAddress(FileAddress)
     */
    public FileAddress seek(final SeekableByteChannel byteChannel, final FileAddress address)
            throws IOException {
        Preconditions.checkArgument(byteChannel != null, "null byteChannel");

        final FileAddress normalized = normalizeAddress(address);

        if (undefinedAddress.equals(normalized)) {
            throw new HDF5jException.FileAddressException(address, " cannot be used to seek");
        }

        byteChannel.position(normalized.position);

        return normalized;
    }

    /**
     * Encodes the provided address into the {@link ByteBuffer}.
     *
     * @param address address to encode.
     * @param buffer  buffer to put the address bytes.
     *
     * @return normalized address.
     *
     * @throws HDF5jException.FileAddressException if there is a problem encoding the address.
     * @see #normalizeAddress(FileAddress)
     */
    public FileAddress encodeAddress(final FileAddress address, final ByteBuffer buffer) {
        Preconditions.checkArgument(buffer != null, "null buffer");
        // for encoding, we require to be able to write at least addressSize bytes
        Preconditions.checkArgument(buffer.remaining() >= addressSize,
                "required at least %s bytes in the buffer to write an address (only %s)",
                addressSize, buffer.remaining());

        // normalize the address and put the bytes in the buffer
        final FileAddress normalized = normalizeAddress(address);
        buffer.put(normalized.bytes);
        // return the normalized address
        return normalized;
    }

    /**
     * Reads an address from the provided byte buffer.
     *
     * <p>Note: this method consumes {@link #getAddressSize()} bytes.
     *
     * @param buffer buffer to get the bytes for decode the address.
     *
     * @return new file address parsed from the buffer; {@link #getUndefinedAddress()} if undefined.
     *
     * @throws HDF5jException.FileAddressException if there is a problem parsing the address.
     */
    public FileAddress decodeAddress(final ByteBuffer buffer) {
        Preconditions.checkArgument(buffer != null, "null buffer");
        Preconditions.checkArgument(buffer.remaining() == addressSize,
                "at least %s should be available in the provided byte buffer", addressSize);
        final byte[] bytes = new byte[addressSize];
        buffer.get(bytes);
        final FileAddress address = new FileAddress(bytes);
        // return always the cached undefinedAddress to avoid storage of the same object
        return (undefinedAddress.equals(address)) ? undefinedAddress : address;
    }

    /**
     * Decodes the address from the provided position.
     *
     * @param filePosition position in the file to convert to an address; {@code -1} for undefined.
     *
     * @return new file address for the position.
     *
     * @throws HDF5jException.FileAddressException if there is a problem converting the address.
     */
    public FileAddress decodeAddress(final long filePosition) {
        Preconditions.checkArgument(filePosition >= -1,
                "file position cannot be negative (%s) except for undefined address (%s)",
                filePosition, undefinedAddress.position);
        final byte[] convertedArray = BigInteger.valueOf(filePosition).toByteArray();
        if (filePosition == -1) {
            return undefinedAddress;
        } else if (convertedArray.length == addressSize) {
            return new FileAddress(convertedArray);
        } else if (convertedArray.length < addressSize) {
            final ByteBuffer buffer = ByteBuffer.allocate(addressSize);
            // pad the byte array with zeroes at the beginning to get the same position encoded with more bytes
            IntStream.range(convertedArray.length, addressSize).forEach(i -> buffer.put((byte) 0));
            // put the address bytes into the buffer
            buffer.put(convertedArray);
            return new FileAddress(buffer.array());
        } else {
            throw new HDF5jException.FileAddressException(
                    "Position " + filePosition + " cannot be be encoded with " + this);
        }
    }

    /**
     * Normalizes the address by encoding with the manager address size ({@link #getAddressSize()}).
     *
     * @param address the address to normalize.
     *
     * @return normalized file address (may be the same object).
     *
     * @throws HDF5jException.FileAddressException if the address cannot be normalized with this
     *                                             manager.
     */
    public FileAddress normalizeAddress(final FileAddress address) {
        Preconditions.checkArgument(address != null, "null address");

        // for undefined address, it should return the same
        if (undefinedAddress.equals(address)) {
            return undefinedAddress;
        }

        if (address.bytes.length == getAddressSize()) {
            return address;
        }

        try {
            return decodeAddress(address.position);
        } catch (HDF5jException.FileAddressException e) {
            // rethrow to include in the message the address format
            throw new HDF5jException.FileAddressException(address, e.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("%s[addressSize=%s]",
                this.getClass().getSimpleName(), getAddressSize());
    }
}
