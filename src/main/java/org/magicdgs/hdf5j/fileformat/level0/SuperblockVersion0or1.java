package org.magicdgs.hdf5j.fileformat.level0;

import com.google.common.base.Preconditions;

import java.math.BigInteger;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
abstract class SuperblockVersion0or1 extends Superblock {

    // unsigned short
    private final int groupLeafNodeK;
    // unsigned short
    private final int groupInternalNodeK;

    // addresses stored as BigInteger constructed with a byte[sizeOfOffsets] read in a little-endian order
    private final BigInteger addressOfGlobalFreeSpaceIndex;
    private final BigInteger driverInformationBlockAddress;

    // integer
    private final int rootGroupSymbolTableEntry;

    private SuperblockVersion0or1(final int version,
            final int sizeOfOffsets, final int sizeOfLengths,
            final int fileConsistencyFlags, final BigInteger baseAddress,
            final BigInteger endOfFileAddress, final BigInteger addressOfGlobalFreeSpaceIndex,
            final BigInteger driverInformationBlockAddress,
            final int groupLeafNodeK, final int groupInternalNodeK,
            final int rootGroupSymbolTableEntry) {
        super(version, sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress, endOfFileAddress);

        // TODO: add checking for non-null !!
        this.addressOfGlobalFreeSpaceIndex = addressOfGlobalFreeSpaceIndex;
        this.driverInformationBlockAddress = driverInformationBlockAddress;

        this.groupLeafNodeK = groupLeafNodeK;
        Preconditions.checkArgument(groupLeafNodeK > 0,
                "Group Leaf Node K should be larger than 0: %s", groupLeafNodeK);
        this.groupInternalNodeK = groupInternalNodeK;
        Preconditions.checkArgument(groupInternalNodeK > 0,
                "Group Internal Node K should be larger than 0: %s", groupInternalNodeK);
        this.rootGroupSymbolTableEntry = rootGroupSymbolTableEntry;

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
    public final BigInteger getAddressOfGlobalFreeSpaceIndex() {
        return addressOfGlobalFreeSpaceIndex;
    }

    @Override
    public final BigInteger getDriverInformationBlockAddress() {
        return driverInformationBlockAddress;
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
    public final int getRootGroupSymbolTableEntry() {
        return rootGroupSymbolTableEntry;
    }

    static class SuperblockVersion0 extends SuperblockVersion0or1 {

        SuperblockVersion0(int sizeOfOffsets, int sizeOfLengths,
                int fileConsistencyFlags, BigInteger baseAddress,
                BigInteger endOfFileAddress, BigInteger addressOfGlobalFreeSpaceIndex,
                BigInteger driverInformationBlockAddress, int groupLeafNodeK,
                int groupInternalNodeK, int rootGroupSymbolTableEntry) {
            super(0, sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress, endOfFileAddress,
                    addressOfGlobalFreeSpaceIndex, driverInformationBlockAddress, groupLeafNodeK,
                    groupInternalNodeK, rootGroupSymbolTableEntry);
        }
    }

    static class SuperblockVersion1 extends SuperblockVersion0or1 {

        // unsigned short
        private final int indexedStorageInternalNodeK;

        SuperblockVersion1(int sizeOfOffsets, int sizeOfLengths,
                int fileConsistencyFlags, BigInteger baseAddress,
                BigInteger endOfFileAddress, BigInteger addressOfGlobalFreeSpaceIndex,
                BigInteger driverInformationBlockAddress, int groupLeafNodeK,
                int groupInternalNodeK, int rootGroupSymbolTableEntry, int indexedStorageInternalNodeK) {
            super(1, sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress, endOfFileAddress,
                    addressOfGlobalFreeSpaceIndex, driverInformationBlockAddress, groupLeafNodeK,
                    groupInternalNodeK, rootGroupSymbolTableEntry);
            this.indexedStorageInternalNodeK = indexedStorageInternalNodeK;
            Preconditions.checkArgument(indexedStorageInternalNodeK > 0,
                    "Index Storage Internal Node K should be larger than 0: %s",
                    indexedStorageInternalNodeK);
        }

        @Override
        public int getIndexedStorageInternalNodeK() {
            return this.indexedStorageInternalNodeK;
        }
    }
}
