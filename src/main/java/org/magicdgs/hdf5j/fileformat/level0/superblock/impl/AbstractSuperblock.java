package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.HDF5Constants;
import org.magicdgs.hdf5j.fileformat.level0.superblock.Superblock;
import org.magicdgs.hdf5j.io.address.FileAddress;
import org.magicdgs.hdf5j.io.address.FileAddressManager;
import org.magicdgs.hdf5j.utils.HDF5jException;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 * Barebone implementation of superblock common to versions 0 to 3.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote all the methods exclusive for a subset of versions throws by default a
 * {@link HDF5jException.SuperblockVersionException} to make sure that the behaviour is correct if
 * new ones should be implemented. They will be overrided if required in sub-classes.
 */
abstract class AbstractSuperblock implements Superblock {

    /** Logger for the class. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // contains the size of offsets for get addresses
    /** Manager for encoding/decoding the file addresses. */
    protected final FileAddressManager fileAddressManager;

    // always present fields
    private final int sizeOfLengths;

    // present for all, but different definitions
    private final int fileConsistencyFlags;

    private final FileAddress baseAddress;
    private final FileAddress endOfFileAddress;


    AbstractSuperblock(final FileAddressManager fileAddressManager, final int sizeOfLengths,
            int fileConsistencyFlags,
            final FileAddress baseAddress, final FileAddress endOfFileAddress) {
        this.fileAddressManager = fileAddressManager;
        this.sizeOfLengths = sizeOfLengths;
        this.fileConsistencyFlags = fileConsistencyFlags;
        this.baseAddress = baseAddress;
        this.endOfFileAddress = endOfFileAddress;
    }

    @Override
    public int getSizeOfOffsets() {
        return fileAddressManager.getAddressSize();
    }

    @Override
    public int getSizeOfLengths() {
        return sizeOfLengths;
    }

    @Override
    public int getFileConsistencyFlags() {
        return fileConsistencyFlags;
    }

    @Override
    public FileAddress getBaseAddress() {
        return baseAddress;
    }


    @Override
    public FileAddress getEndOfFileAddress() {
        return endOfFileAddress;
    }

    @Override
    public int getFreeSpaceManagerVersionNumber() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Free Space Manager Version Number is unsupported");
    }

    @Override
    public int getRootSymbolTableEntryVersionNumber() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Root Symbol Table Entry Version Number is unsupported");
    }

    @Override
    public int getSharedHeaderMessageFormatVersionNumber() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Shared Header Message Format Version Number is unsupported");
    }

    @Override
    public int getGroupLeafNodeK() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Group Leaf Node K is unsupported");
    }

    @Override
    public int getGroupInternalNodeK() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Group Internal Node K is unsupported");
    }

    @Override
    public int getIndexedStorageInternalNodeK() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Indexed Storage Internal Node K is unsupported");
    }

    @Override
    public FileAddress getAddressOfGlobalFreeSpaceIndex() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Address of Global Free Space Index is unsupported");
    }

    @Override
    public FileAddress getDriverInformationBlockAddress() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Driver Information Block Address is unsupported");
    }

    @Override
    public Object getRootGroupSymbolTableEntry() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Root Group Symbol Table Entry is unsupported");
    }

    @Override
    public FileAddress getSuperblockExtensionAddress() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Superblock Extension Address is unsupported");
    }

    @Override
    public FileAddress getRootGroupObjectHeaderAddress() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Root Group Object Header Address is unsupported");
    }

    @Override
    public int getSuperblockChecksum() {
        throw new HDF5jException.SuperblockVersionException(this,
                "Superblock Checksum is unsupported");
    }

    /**
     * Gets a byte buffer with the 8 bytes HDF5 signature ({@link HDF5Constants#FORMAT_SIGNATURE}).
     * <p>
     * This method may be used as the first step in {@link #write(SeekableByteChannel)} to both
     * add the superblock signature and check if the {@link SeekableByteChannel} is writable.
     *
     * @return new buffer prepared for writing with the superblock signature.
     */
    @VisibleForTesting
    static ByteBuffer getSignatureBufferToWrite() {
        final ByteBuffer buffer =
                ByteBuffer.allocate(Byte.SIZE * HDF5Constants.FORMAT_SIGNATURE.length);
        // put the signature as an unsinged bytes
        for (final int unsignedByte : HDF5Constants.FORMAT_SIGNATURE) {
            buffer.put((byte) unsignedByte);
        }
        // prepare the buffer to write
        buffer.flip();
        return buffer;
    }

}
