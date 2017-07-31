package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.io.address.FileAddress;
import org.magicdgs.hdf5j.io.address.FileAddressManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;

/**
 * Superblock Version 2 and 3.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class SuperblockVersion2 extends AbstractSuperblock {
    // version 2 and 3 (null if other versions)
    private final FileAddress superblockExtensionAddress;
    private final FileAddress rootGroupObjectHeaderAddress;

    // TODO: this shouldn't be provided, but compute
    // TODO: after construction, if can be checked if it is the same
    private final int superblockChecksum;


    SuperblockVersion2(FileAddressManager sizeOfOffsets, int sizeOfLengths,
            int fileConsistencyFlags,
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
        // TODO: if so, we need to extend this class an override the getVersionNumber
        // TODO: because the only difference is that the consistency-flags are set
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
    public void write(final SeekableByteChannel byteChannel) throws IOException {
        // first try to write the header to check if the channel is writable
        byteChannel.write(getSignatureBufferToWrite());

        // allocate a buffer for the header (depends on the size of offsets for addresses)
        // it should be ordered as a little-endian
        final ByteBuffer buffer = ByteBuffer
                // 4 bytes (version number, size of offset/lengths, file consistency flags)
                // 4 addresses (size of offsets)
                // 1 int (superblock checksum)
                .allocate(Byte.SIZE * 4 + Byte.SIZE + getSizeOfOffsets() * 4 + Integer.SIZE * 1)
                // the HDF5 format is little-endian encoded
                .order(ByteOrder.LITTLE_ENDIAN);

        // first put the version
        buffer.put((byte) getVersionNumber())
                .put((byte) getSizeOfOffsets())
                .put((byte) getSizeOfLengths())
                // TODO: check that the checksum is correct?
                .put((byte)getFileConsistencyFlags());

        // encode the addresses using the address factory
        fileAddressManager.encodeAddress(getBaseAddress(), buffer);
        fileAddressManager.encodeAddress(getSuperblockExtensionAddress(), buffer);
        fileAddressManager.encodeAddress(getEndOfFileAddress(), buffer);
        fileAddressManager.encodeAddress(getRootGroupObjectHeaderAddress(), buffer);

        // flip for writing and write
        byteChannel.write((ByteBuffer) buffer.flip());
    }
}
