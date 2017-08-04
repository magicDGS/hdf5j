package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.io.FileAddressManager;

/**
 * Extension of Superblock Version 0, with support for {@link #getIndexedStorageInternalNodeK()}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
class SuperblockVersion1 extends SuperblockVersion0 {

    // unsigned short
    private final int indexedStorageInternalNodeK;

    SuperblockVersion1(final FileAddressManager sizeOfOffsets, final int sizeOfLengths, final int fileConsistencyFlags,
            final int groupLeafNodeK, final int groupInternalNodeK, final int indexedStorageInternalNodeK,
            final FileAddressManager.FileAddress baseAddress, final FileAddressManager.FileAddress endOfFileAddress,
            FileAddressManager.FileAddress addressOfGlobalFreeSpaceIndex,
            FileAddressManager.FileAddress driverInformationBlockAddress,
            Object rootGroupSymbolTableEntry) {
        super(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, groupLeafNodeK,
                groupInternalNodeK,
                baseAddress, endOfFileAddress, addressOfGlobalFreeSpaceIndex,
                driverInformationBlockAddress, rootGroupSymbolTableEntry);
        this.indexedStorageInternalNodeK = indexedStorageInternalNodeK;
    }

    public int getVersionNumber() {
        return 1;
    }

    @Override
    public int getIndexedStorageInternalNodeK() {
        return indexedStorageInternalNodeK;
    }
}
