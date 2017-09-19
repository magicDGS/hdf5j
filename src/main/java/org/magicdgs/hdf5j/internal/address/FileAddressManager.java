package org.magicdgs.hdf5j.internal.address;

import org.magicdgs.hdf5j.fileformat.FileAddress;
import org.magicdgs.hdf5j.utils.exceptions.FileAddressException;

import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.nio.ByteBuffer;
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
        // cache an undefined address
        this.undefinedAddress = FileAddressImpl.getUndefinedAddressForSize(addressSize);
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
     * Encodes the provided address into the {@link ByteBuffer}.
     *
     * @param address address to encode.
     * @param buffer  buffer to put the address bytes.
     *
     * @return normalized address.
     *
     * @throws FileAddressException if there is a problem encoding the address.
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
        buffer.put(normalized.asByteArray());
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
     * @throws FileAddressException if there is a problem parsing the address.
     */
    public FileAddress decodeAddress(final ByteBuffer buffer) {
        Preconditions.checkArgument(buffer != null, "null buffer");
        Preconditions.checkArgument(buffer.remaining() == addressSize,
                "at least %s should be available in the provided byte buffer", addressSize);
        final byte[] bytes = new byte[addressSize];
        buffer.get(bytes);
        final FileAddress address = new FileAddressImpl(bytes);
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
     * @throws FileAddressException if there is a problem converting the address.
     */
    public FileAddress decodeAddress(final long filePosition) {
        Preconditions.checkArgument(filePosition >= -1,
                "file position cannot be negative (%s) except for undefined address (%s)",
                filePosition, undefinedAddress.getFilePointer());
        final byte[] convertedArray = BigInteger.valueOf(filePosition).toByteArray();
        if (filePosition == -1) {
            return undefinedAddress;
        } else if (convertedArray.length == addressSize) {
            return new FileAddressImpl(convertedArray);
        } else if (convertedArray.length < addressSize) {
            final ByteBuffer buffer = ByteBuffer.allocate(addressSize);
            // pad the byte array with zeroes at the beginning to get the same position encoded with more bytes
            IntStream.range(convertedArray.length, addressSize).forEach(i -> buffer.put((byte) 0));
            // put the address bytes into the buffer
            buffer.put(convertedArray);
            return new FileAddressImpl(buffer.array());
        } else {
            throw new FileAddressException(
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
     * @throws FileAddressException if the address cannot be normalized with this
     *                              manager.
     */
    public FileAddress normalizeAddress(final FileAddress address) {
        Preconditions.checkArgument(address != null, "null address");

        // for undefined address, it should return the same
        if (undefinedAddress.equals(address)) {
            return undefinedAddress;
        }

        if (address.getNumberOfBytes() == getAddressSize()) {
            return address;
        }

        try {
            return decodeAddress(address.getFilePointer());
        } catch (FileAddressException e) {
            // rethrow to include in the message the address format
            throw new FileAddressException(address, e.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("%s[addressSize=%s]",
                this.getClass().getSimpleName(), getAddressSize());
    }
}
