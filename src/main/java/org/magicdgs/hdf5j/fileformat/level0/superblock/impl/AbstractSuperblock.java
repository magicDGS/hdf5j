package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.io.FileAddressManager;
import org.magicdgs.hdf5j.fileformat.level0.superblock.Superblock;

/**
 * Barebone implementation of superblock common to versions 0 to 3.
 *
 * @implNote all the methods exclusive for a subset of versions throws by default a TODO: SuperblockVersionException
 * to make sure that the behaviour is correct if new ones should be implemented. They will be overrided if required in sub-classes.
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
abstract class AbstractSuperblock implements Superblock {

    // contains the size of offsets for get addresses
    protected final FileAddressManager addressFactory;

    // always present fields
    private final int sizeOfLengths;

    // present for all, but different definitions
    private final int fileConsistencyFlags;

    // addresses stored as BigInteger constructed with a byte[] read in a little-endian order
    // common addresses
    private final FileAddressManager.FileAddress baseAddress;
    private final FileAddressManager.FileAddress endOfFileAddress;


    AbstractSuperblock(final FileAddressManager addressFactory, final int sizeOfLengths, int fileConsistencyFlags,
           final FileAddressManager.FileAddress baseAddress, final FileAddressManager.FileAddress endOfFileAddress) {
        this.addressFactory = addressFactory;
        this.sizeOfLengths = sizeOfLengths;
        this.fileConsistencyFlags = fileConsistencyFlags;
        this.baseAddress = baseAddress;
        this.endOfFileAddress = endOfFileAddress;
    }

    @Override
    public int getSizeOfOffsets() {
        return addressFactory.getAddressSize();
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
    public FileAddressManager.FileAddress getBaseAddress() {
        return baseAddress;
    }


    @Override
    public FileAddressManager.FileAddress getEndOfFileAddress() {
        return endOfFileAddress;
    }

    @Override
    public int getFreeSpaceManagerVersionNumber() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRootSymbolTableEntryVersionNumber() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSharedHeaderMessageFormatVersionNumber() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGroupLeafNodeK() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGroupInternalNodeK() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIndexedStorageInternalNodeK() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public FileAddressManager.FileAddress getAddressOfGlobalFreeSpaceIndex() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public FileAddressManager.FileAddress getDriverInformationBlockAddress() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getRootGroupSymbolTableEntry() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public FileAddressManager.FileAddress getSuperblockExtensionAddress() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public FileAddressManager.FileAddress getRootGroupObjectHeaderAddress() {
        // TODO: change
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSuperblockChecksum() {
        // TODO: change
        throw new UnsupportedOperationException();
    }
}
