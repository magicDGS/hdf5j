package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.io.address.FileAddress;
import org.magicdgs.hdf5j.io.address.FileAddressManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;

/**
 * Extension of Superblock Version 0, with support for {@link #getIndexedStorageInternalNodeK()}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class SuperblockVersion1 extends SuperblockVersion0 {

    // unsigned short
    private final int indexedStorageInternalNodeK;

    SuperblockVersion1(final FileAddressManager sizeOfOffsets, final int sizeOfLengths,
            final int fileConsistencyFlags,
            final int groupLeafNodeK, final int groupInternalNodeK,
            final int indexedStorageInternalNodeK,
            final FileAddress baseAddress,
            final FileAddress endOfFileAddress,
            FileAddress driverInformationBlockAddress,
            Object rootGroupSymbolTableEntry) {
        super(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, groupLeafNodeK,
                groupInternalNodeK,
                baseAddress, endOfFileAddress,
                driverInformationBlockAddress, rootGroupSymbolTableEntry);
        this.indexedStorageInternalNodeK = indexedStorageInternalNodeK;
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }

    @Override
    public int getIndexedStorageInternalNodeK() {
        return indexedStorageInternalNodeK;
    }

    @Override
    public void write(final SeekableByteChannel byteChannel) throws IOException {
        // first try to write the header to check if the channel is writable
        byteChannel.write(getSignatureBufferToWrite());

        // allocate a buffer for the header (depends on the size of offsets for addresses)
        // it should be ordered as a little-endian
        final ByteBuffer buffer = ByteBuffer
                // 8 bytes (4 versions numbers, 2 reserved field and size of offset/lengths)
                // 4 shorts (nodeK fields + reserved)
                // 1 int (file consistency flag, in versions 0 and 1 is not a byte)
                // 4 addresses (encoded with size of offsets)
                // TODO: we should also allocate the RootGroupSymbolTableEntry size
                .allocate(Byte.SIZE * 8 + Short.SIZE * 4 + Integer.SIZE * 1 + getSizeOfOffsets() * 4 * Byte.SIZE)
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
                .putInt(getFileConsistencyFlags())
                .putShort((short) getIndexedStorageInternalNodeK())
                .putShort((short) 0); // reserved

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
