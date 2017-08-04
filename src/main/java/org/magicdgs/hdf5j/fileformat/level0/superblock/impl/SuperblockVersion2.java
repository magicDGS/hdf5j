package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.io.FileAddressManager;
import org.magicdgs.hdf5j.io.FileAddressManager.FileAddress;

import com.google.common.io.LittleEndianDataOutputStream;

import java.io.IOException;

/**
 * Superblock Version 2 and 3.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
class SuperblockVersion2 extends AbstractSuperblock {
    // version 2 and 3 (null if other versions)
    private final FileAddress superblockExtensionAddress;
    private final FileAddress rootGroupObjectHeaderAddress;

    private final int superblockChecksum;


    SuperblockVersion2(FileAddressManager sizeOfOffsets, int sizeOfLengths, int fileConsistencyFlags,
            final FileAddress baseAddress, final FileAddress endOfFileAddress,
            final FileAddress superblockExtensionAddress,
            final FileAddress rootGroupObjectHeaderAddress,
            final int superblockChecksum) {
        super(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress,
                endOfFileAddress);
        this.superblockExtensionAddress = superblockExtensionAddress;
        this.rootGroupObjectHeaderAddress = rootGroupObjectHeaderAddress;
        this.superblockChecksum = superblockChecksum;
    }

    @Override
    public int getVersionNumber() {
        // TODO: does version 3 have a different version number?
        return 2;
    }

    @Override
    public FileAddress getSuperblockExtensionAddress() {
        return superblockExtensionAddress;
    }

    @Override
    public FileAddress getRootGroupObjectHeaderAddress() {
        return rootGroupObjectHeaderAddress;
    }

    @Override
    public int getSuperblockChecksum() {
        return superblockChecksum;
    }

    @Override
    public void write(LittleEndianDataOutputStream littleEndianDataOutputStream)
            throws IOException {
        // TODO: implement
    }
}
