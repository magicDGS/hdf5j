package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.HDF5Constants;
import org.magicdgs.hdf5j.HDF5Utils;
import org.magicdgs.hdf5j.io.FileAddressManager;
import org.magicdgs.hdf5j.fileformat.level0.superblock.Superblock;
import org.magicdgs.hdf5j.io.FileAddressManager.FileAddress;

import com.google.common.base.Preconditions;
import com.google.common.io.LittleEndianDataInputStream;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Simple codec to read/write superblocks.
 *
 * TODO: class in development
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class SuperblockCodec {

    /**
     * Reads the Superblock from a data input (in little-endian format), starting from the provided
     * offset.
     *
     * @param dataInput input data (little-endian).
     * @param offset    number of bytes to skip from the beginning of the file. If {@code 0}, start
     *                  from the beginning of the file.
     *
     * @throws IOException if an IO error occurs
     */
    // TODO: split in various
    public static Superblock readFromDataInputStream(final LittleEndianDataInputStream dataInput,
            final long offset)
            throws IOException {
        Preconditions.checkArgument(dataInput != null, "null data input stream");
        // TODO: should this be a warning?
        Preconditions.checkArgument(dataInput.skip(offset) == offset,
                "skipped different number of bytes from the file");
        Preconditions.checkArgument(HDF5Utils.isHDF5Stream(dataInput),
                "input stream should have the HDF5 signature");
        // skip the bytes of the signature (ignore because it was already tested)
        dataInput.skip(HDF5Constants.FORMAT_SIGNATURE.length);

        // start parsing
        final int version = dataInput.readUnsignedByte();
        // set the variables
        final FileAddressManager sizeOfOffsets;
        final int sizeOfLengths;
        final int groupInternalNodeK;
        final int groupLeafNodeK;
        final int fileConsistencyFlags;
        final int indexedStorageInternalNodeK;
        final FileAddress baseAddress;
        final FileAddress addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress;
        final FileAddress endOfFileAddress;
        final FileAddress driverInformationBlockAddressOrRootGroupObjectHeaderAddress;
        final int rootGroupSymbolTableEntryOrSuperblockChecksum;


        // next bytes are only present in version 0 and 1
        if (version < 2) {
            // only check if the value of the unsigned byte is correct for the Free Space Manager, Root Symbol Table Entry and Shared Header Message
            Preconditions.checkArgument(Superblock.FREE_SPACE_MANAGER_VERSION_NUMBER == dataInput.readUnsignedByte(),
                    "Invalid value at Free Space Manager Version Number byte");
            Preconditions.checkArgument(Superblock.ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER == dataInput.readUnsignedByte(),
                    "Invalid value at Root Symbol Table Entry Version Number byte");
            // reserved byte (should be 0) TODO: should we check?
            dataInput.readUnsignedByte();
            Preconditions.checkArgument(Superblock.SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER == dataInput.readUnsignedByte(),
                    "Invalid value at Shared Header Message Format Version Number byte");
        }
        // next bytes are in the same order for all versions
        sizeOfOffsets = new FileAddressManager(dataInput.readUnsignedByte());
        sizeOfLengths = dataInput.readUnsignedByte();
        // for versions 0 and 1, and 2 and 3, there is divergence here
        if (version < 2) {
            // version 0 and 1
            // reserved byte
            dataInput.readUnsignedByte();
            // nodes are 2 bytes (short)  TODO: we are reading it as unsigned, but it is true?
            groupLeafNodeK = dataInput.readUnsignedShort();
            Preconditions.checkArgument(groupLeafNodeK > 0,
                    "Group Leaf Node K should be larger than 0: %s", groupLeafNodeK);
            groupInternalNodeK = dataInput.readUnsignedByte();
            Preconditions.checkArgument(groupInternalNodeK > 0,
                    "Group Internal Node K should be larger than 0: %s", groupInternalNodeK);
            // read the file consistency tags (for this version, it has a length of 4 bytes - that's it, integer)
            fileConsistencyFlags = dataInput.readInt();
        } else {
            // mark as unset the Group Leaf Node K and Group Internal Node K
            groupLeafNodeK = 0;
            groupInternalNodeK = 0;
            // read the file consistency tags (only valid for version 3)
            fileConsistencyFlags = dataInput.readUnsignedByte();
        }
        // indexedStorageInternalNodeK only for version 1
        if (version == 1) {
            // 2 bytes
            indexedStorageInternalNodeK = dataInput.readUnsignedShort();
            Preconditions.checkArgument(indexedStorageInternalNodeK > 0,
                    "Index Storage Internal Node K should be larger than 0: %s",
                    indexedStorageInternalNodeK);
            // and ignore the next 2 bytes
            dataInput.readUnsignedShort();
        } else {
            // mark as unset
            indexedStorageInternalNodeK = -1;
        }
        // reading addresses  TODO: adjust based on the offset?
        // base address
        baseAddress = sizeOfOffsets.decodeAddress(dataInput);
        // for version 0 and 1, free space address; for version 2 and 3, superblock extension address
        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress = sizeOfOffsets.decodeAddress(dataInput);
        // now it comes the end of file address
        endOfFileAddress = sizeOfOffsets.decodeAddress(dataInput);
        // for version 0 and 1, driver information block address; for version 2 and 3, superblock extension address
        driverInformationBlockAddressOrRootGroupObjectHeaderAddress = sizeOfOffsets.decodeAddress(dataInput);

        // read the last bytes of the superblock (integer value)
        rootGroupSymbolTableEntryOrSuperblockChecksum = dataInput.readInt();

        switch (version) {
            case 0:
                new SuperblockVersion0(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags,
                        groupLeafNodeK, groupInternalNodeK,
                        baseAddress, endOfFileAddress,
                        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress,
                        driverInformationBlockAddressOrRootGroupObjectHeaderAddress,
                        // TODO: this should be an object readed properly
                        rootGroupSymbolTableEntryOrSuperblockChecksum);
            case 1:
                new SuperblockVersion1(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags,
                        groupLeafNodeK, groupInternalNodeK, indexedStorageInternalNodeK,
                        baseAddress, endOfFileAddress,
                        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress,
                        driverInformationBlockAddressOrRootGroupObjectHeaderAddress,
                        // TODO: this should be an object readed properly
                        rootGroupSymbolTableEntryOrSuperblockChecksum);
            case 2:
                new SuperblockVersion2(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags,
                        baseAddress, endOfFileAddress,
                        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress,
                        driverInformationBlockAddressOrRootGroupObjectHeaderAddress,
                        rootGroupSymbolTableEntryOrSuperblockChecksum);
            default:
                // TODO: is version 3 a valid one?
                throw new IllegalArgumentException("Unknown version");
        }
    }

    // reads a little-endian input into a BigInteger
    private static BigInteger readLittleEndianBigInteger(
            final LittleEndianDataInputStream dataInput, final int size) throws IOException {
        final byte[] littleEndian = new byte[size];
        for (int i = size - 1; i <= 0; i--) {
            littleEndian[i] = dataInput.readByte();
        }
        return new BigInteger(littleEndian);
    }
}
