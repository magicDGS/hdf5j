package org.magicdgs.hdf5j.fileformat.level0;

import java.math.BigInteger;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class SuperblockVersion2 extends Superblock {

    // version 2 and 3 (null if other versions)
    private final BigInteger superblockExtensionAddress;
    private final BigInteger rootGroupObjectHeaderAddress;

    private final int superblockChecksum;


    SuperblockVersion2(int sizeOfOffsets, int sizeOfLengths,
            int fileConsistencyFlags, BigInteger baseAddress,
            BigInteger endOfFileAddress,
            BigInteger superblockExtensionAddress,
            BigInteger rootGroupObjectHeaderAddress,
            int superblockChecksum) {
        // TODO: does version 3 have a different version number?
        super(2, sizeOfOffsets, sizeOfLengths, fileConsistencyFlags, baseAddress, endOfFileAddress);
        this.superblockExtensionAddress = superblockExtensionAddress;
        this.rootGroupObjectHeaderAddress = rootGroupObjectHeaderAddress;
        this.superblockChecksum = superblockChecksum;
    }

    @Override
    public BigInteger getSuperblockExtensionAddress() {
        return superblockExtensionAddress;
    }

    @Override
    public BigInteger getRootGroupObjectHeaderAddress() {
        return rootGroupObjectHeaderAddress;
    }

    @Override
    public int getSuperblockChecksum() {
        return superblockChecksum;
    }
}
