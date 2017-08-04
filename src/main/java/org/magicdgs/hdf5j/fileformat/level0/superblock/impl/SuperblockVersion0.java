package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.HDF5Constants;
import org.magicdgs.hdf5j.io.FileAddressManager;
import org.magicdgs.hdf5j.io.FileAddressManager.FileAddress;

import com.google.common.io.LittleEndianDataOutputStream;

import java.io.IOException;

/**
 * Implementation of Superblock Version 0.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
class SuperblockVersion0 extends AbstractSuperblock {

    // unsigned short
    private final int groupLeafNodeK;
    // unsigned short
    private final int groupInternalNodeK;

    // addresses stored as BigInteger constructed with a byte[sizeOfOffsets] read in a little-endian order
    private final FileAddress addressOfGlobalFreeSpaceIndex;
    private final FileAddress driverInformationBlockAddress;

    // TODO: add link to the Symbol Table Entry class once it is implemented
    // TODO: this should return the Symbol Table Entry
    private final Object rootGroupSymbolTableEntry;

    SuperblockVersion0(FileAddressManager sizeOfOffsets, int sizeOfLengths, int fileConsistencyFlags,
            final int groupLeafNodeK, final int groupInternalNodeK,
            final FileAddress baseAddress, final FileAddress endOfFileAddress,
            final FileAddress addressOfGlobalFreeSpaceIndex,
            final FileAddress driverInformationBlockAddress,
            final Object rootGroupSymbolTableEntry) {
        super(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress,
                endOfFileAddress);
        this.groupLeafNodeK = groupLeafNodeK;
        this.groupInternalNodeK = groupInternalNodeK;
        this.addressOfGlobalFreeSpaceIndex = addressOfGlobalFreeSpaceIndex;
        this.driverInformationBlockAddress = driverInformationBlockAddress;
        this.rootGroupSymbolTableEntry = rootGroupSymbolTableEntry;
    }

    @Override
    public int getVersionNumber() {
        return 0;
    }

    @Override
    public int getFreeSpaceManagerVersionNumber() {
        return ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER;
    }

    @Override
    public int getRootSymbolTableEntryVersionNumber() {
        return ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER;
    }

    @Override
    public int getSharedHeaderMessageFormatVersionNumber() {
        return SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER;
    }

    @Override
    public final FileAddress getAddressOfGlobalFreeSpaceIndex() {
        return addressOfGlobalFreeSpaceIndex;
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
    public void write(final LittleEndianDataOutputStream littleEndianDataOutputStream)
            throws IOException {
        // write the signature as unsigned bytes (8-bits)
        for (final int unsignedByte: HDF5Constants.FORMAT_SIGNATURE) {
            littleEndianDataOutputStream.writeByte(unsignedByte);
        }
        // versions and reserved (4 bytes)
        littleEndianDataOutputStream.writeByte(getVersionNumber());
        littleEndianDataOutputStream.writeByte(getFreeSpaceManagerVersionNumber());
        littleEndianDataOutputStream.writeByte(getRootSymbolTableEntryVersionNumber());
        littleEndianDataOutputStream.writeByte(0);

        // another version, sizes and reserved (4 bytes)
        littleEndianDataOutputStream.writeByte(getSharedHeaderMessageFormatVersionNumber());
        littleEndianDataOutputStream.writeByte(getSizeOfOffsets());
        littleEndianDataOutputStream.writeByte(getSizeOfLengths());
        littleEndianDataOutputStream.writeByte(0);

        // write B-Trees information
        littleEndianDataOutputStream.writeShort(getGroupLeafNodeK());
        littleEndianDataOutputStream.writeShort(getGroupInternalNodeK());

        // consistency tags are int in superblock 0
        littleEndianDataOutputStream.writeInt(getFileConsistencyFlags());

        // TODO: check if writting the addresses like this is correct

    }

    @Override
    public final int getGroupLeafNodeK() {
        return groupLeafNodeK;
    }

    @Override
    public final int getGroupInternalNodeK() {
        return groupInternalNodeK;
    }
}
