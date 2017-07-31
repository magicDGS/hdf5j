package org.magicdgs.hdf5j.io.address;

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
     * @throws HDF5jException.FileAddressException if the address cannot be handled or it is
     *                                             undefined.
     * @throws IOException                         if an IO error occurs.
     */
    public void seek(final SeekableByteChannel byteChannel, final FileAddress address)
            throws IOException {
        if (undefinedAddress.equals(address)) {
            throw new HDF5jException.FileAddressException(address, " cannot be used to seek");
        }
        byteChannel.position(validateAddress(address).position);
    }

    /**
     * Encodes the provided address into the {@link ByteBuffer}.
     *
     * @param address address to encode.
     * @param buffer  buffer to put the address bytes.
     *
     * @return the same buffer passed to the method.
     *
     * @throws HDF5jException.FileAddressException if there is a problem encoding the address.
     */
    public ByteBuffer encodeAddress(final FileAddress address, final ByteBuffer buffer) {
        // validate the address to be used
        final FileAddress validated = validateAddress(address);

        Preconditions.checkArgument(buffer != null, "null buffer");
        // for encoding, we require to be able to write at least addressSize bytes
        Preconditions.checkArgument(buffer.remaining() >= addressSize,
                "required at least %s bytes in the buffer to write an address", addressSize);

        if (validated.equals(undefinedAddress)) {
            // if it is undefined, it is independent of the size of the arrays
            buffer.put(undefinedAddress.bytes);
        } else if (validated.bytes.length == addressSize) {
            buffer.put(validated.bytes);
        } else if (validated.bytes.length < addressSize) {
            // pad the byte array with zeroes at the beginning to get the same position encoded with more bytes
            IntStream.range(validated.bytes.length, addressSize).forEach(i -> buffer.put((byte) 0));
            // put the address bytes into the buffer
            buffer.put(validated.bytes);
        } else {
            // this should not happen
            throw new HDF5jException("BUG: should not reach");
        }

        return buffer;
    }

    /**
     * Reads an address from the provided byte buffer.
     *
     * <p>Note: this method consumes {@link #getAddressSize()} bytes.
     *
     * @param buffer buffer to get the bytes for decode the address.
     *
     * @return new file address parsed from the buffer.
     *
     * @throws HDF5jException.FileAddressException if there is a problem parsing the address.
     */
    public FileAddress decodeAddress(final ByteBuffer buffer) {
        Preconditions.checkArgument(buffer != null, "null buffer");
        Preconditions.checkArgument(buffer.remaining() == addressSize,
                "at least %s should be available in the provided byte buffer", addressSize);
        final byte[] bytes = new byte[addressSize];
        buffer.get(bytes);
        return new FileAddress(bytes);
    }

    /**
     * Checks if the address provided is correctly sized with respect to the factory.
     *
     * @param address the address to check.
     *
     * @return the same file address if it fits into the number of bytes for the manager; modified
     * file address adjusted to the size of this manager.
     *
     * @throws HDF5jException.FileAddressException if the address cannot be handled with the
     *                                             manager.
     */
    public FileAddress validateAddress(final FileAddress address) {
        Preconditions.checkArgument(address != null, "null address");

        // for undefined address, it should return the same
        if (undefinedAddress.equals(address)) {
            return undefinedAddress;
        }

        if (address.bytes.length <= getAddressSize()) {
            return address;
        }

        // get the minimum amount of bytes using the BigInteger, to check if we are able to pad
        // with zeroes
        final byte[] convertedArray = BigInteger.valueOf(address.position).toByteArray();
        if (convertedArray.length <= addressSize) {
            return new FileAddress(convertedArray);
        }

        throw new HDF5jException.FileAddressException(address, "cannot be handled with " + this);
    }


    @Override
    public String toString() {
        return String.format("%s[addressSize=%s]",
                this.getClass().getSimpleName(), getAddressSize());
    }
}
