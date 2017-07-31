package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.io.address.FileAddress;
import org.magicdgs.hdf5j.io.address.FileAddressManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;

/**
 * Implementation of Superblock Version 0.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class SuperblockVersion0 extends AbstractSuperblock {

    // unsigned short
    private final int groupLeafNodeK;
    // unsigned short
    private final int groupInternalNodeK;

    private final FileAddress driverInformationBlockAddress;

    // TODO: add link to the Symbol Table Entry class once it is implemented
    // TODO: this should return the Symbol Table Entry
    private final Object rootGroupSymbolTableEntry;

    SuperblockVersion0(FileAddressManager sizeOfOffsets, int sizeOfLengths,
            int fileConsistencyFlags,
            final int groupLeafNodeK, final int groupInternalNodeK,
            final FileAddress baseAddress, final FileAddress endOfFileAddress,
            final FileAddress driverInformationBlockAddress,
            final Object rootGroupSymbolTableEntry) {
        super(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress,
                endOfFileAddress);
        this.groupLeafNodeK = groupLeafNodeK;
        this.groupInternalNodeK = groupInternalNodeK;
        this.driverInformationBlockAddress = driverInformationBlockAddress;
        this.rootGroupSymbolTableEntry = rootGroupSymbolTableEntry;
    }

    @Override
    public int getVersionNumber() {
        return 0;
    }

    @Override
    public int getFreeSpaceManagerVersionNumber() {
        return FREE_SPACE_MANAGER_VERSION_NUMBER;
    }

    @Override
    public int getRootSymbolTableEntryVersionNumber() {
        return ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER;
    }

    @Override
    public int getSharedHeaderMessageFormatVersionNumber() {
        return SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER;
    }

    /** Returns the undefinded address ({@link FileAddressManager#getUndefinedAddress()}. */
    @Override
    public final FileAddress getAddressOfGlobalFreeSpaceIndex() {
        return fileAddressManager.getUndefinedAddress();
    }

    @Override
    public final FileAddress getDriverInformationBlockAddress() {
        return driverInformationBlockAddress;
    }

    @Override
    public Object getRootGroupSymbolTableEntry() {
        return rootGroupSymbolTableEntry;
    }

    @Override
    public final int getGroupLeafNodeK() {
        return groupLeafNodeK;
    }

    @Override
    public final int getGroupInternalNodeK() {
        return groupInternalNodeK;
    }

    @Override
    public void write(final SeekableByteChannel byteChannel)
            throws IOException {
        // first try to write the header to check if the channel is writable
        byteChannel.write(getSignatureBufferToWrite());

        // allocate a buffer for the header (depends on the size of offsets for addresses)
        // it should be ordered as a little-endian
        final ByteBuffer buffer = ByteBuffer
                // 8 bytes (4 versions numbers, 2 reserved field and size of offset/lengths)
                // 2 shorts (nodeKs)
                // 1 int (file consistency flag, in versions 0 and 1 is not a byte)
                // 4 addresses (encoded with size of offsets)
                // TODO: we should also allocate the RootGroupSymbolTableEntry size
                .allocate(Byte.SIZE * 8 + Short.SIZE * 2 + Integer.SIZE * 1
                        + getSizeOfOffsets() * 4 * Byte.SIZE)
                // the HDF5 format is little-endian encoded
                .order(ByteOrder.LITTLE_ENDIAN);

        // first put the version
        buffer.put((byte) getVersionNumber())
                .put((byte) getFreeSpaceManagerVersionNumber())
                .put((byte) getRootSymbolTableEntryVersionNumber())
                .put((byte) 0) // reserved
                .put((byte) getSharedHeaderMessageFormatVersionNumber())
                .put((byte) getSizeOfOffsets())
                .put((byte) getSizeOfLengths())
                .put((byte) 0) // reserved
                .putShort((short) getGroupLeafNodeK())
                .putShort((short) getGroupInternalNodeK())
                // int instad of byte
                .putInt(getFileConsistencyFlags());

        // encode the addresses using teh address factory
        fileAddressManager.encodeAddress(getBaseAddress(), buffer);
        fileAddressManager.encodeAddress(getAddressOfGlobalFreeSpaceIndex(), buffer);
        fileAddressManager.encodeAddress(getEndOfFileAddress(), buffer);
        fileAddressManager.encodeAddress(getDriverInformationBlockAddress(), buffer);

        // TODO: we should add the RootGroupSymbolTableEntry
        logger.warn(
                "INCOMPLETE FEATURE: Root Group Symbol Table Entry is not written yet, so the superblock will be corrupted.");
        // buffer.put(getRootGroupSymbolTableEntry())

        // flip for writing and write
        byteChannel.write((ByteBuffer) buffer.flip());
    }
}
