package org.magicdgs.hdf5j.fileformat.level0;

import org.magicdgs.hdf5j.HDF5Constants;
import org.magicdgs.hdf5j.HDF5Utils;

import com.google.common.base.Preconditions;
import com.google.common.io.LittleEndianDataInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Common layout for the Level 0A - Superblock. Note that the Format Signature is not included in
 * this layout, because it is represented by the constant
 * {@link org.magicdgs.hdf5j.HDF5Constants#FORMAT_SIGNATURE}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote all byte/short fields returns an {@link int} because they are represented as unsigned
 * bytes.
 */
public class Superblock {

    // TODO: documentation
    // only valid value for Free Space Manager Version Number (only for version 0 and 1)
    public static final int FREE_SPACE_MANAGER_VERSION_NUMBER = 0;
    // only valid value for Root Symbol Table Entry Version Number (only for version 0 and 1)
    public static final int ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER = 0;
    // only valid value for Shared Header Message Format Version Number (only for version 0 and 1)
    public static final int SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER = 0;

    /** Logger for the current superblock. */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // always present fields
    private final int version;
    private final int sizeOfOffsets;
    private final int sizeOfLengths;

    // present for all, but different definitions
    private final int fileConsistencyFlags;

    // addresses stored as BigInteger constructed with a byte[] read in a little-endian order
    // common addresses
    private final BigInteger baseAddress;
    private final BigInteger endOfFileAddress;

    Superblock(final int version, final int sizeOfOffsets, final int sizeOfLengths,
            final int fileConsistencyFlags, final BigInteger baseAddress,
            final BigInteger endOfFileAddress) {
        this.version = version;
        this.sizeOfOffsets = sizeOfOffsets;
        this.sizeOfLengths = sizeOfLengths;
        this.fileConsistencyFlags = fileConsistencyFlags;
        this.baseAddress = baseAddress;
        this.endOfFileAddress = endOfFileAddress;
    }


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
        final int sizeOfOffsets;
        final int sizeOfLengths;
        final int groupInternalNodeK;
        final int groupLeafNodeK;
        final int fileConsistencyFlags;
        final int indexedStorageInternalNodeK;
        final BigInteger baseAddress;
        final BigInteger addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress;
        final BigInteger endOfFileAddress;
        final BigInteger driverInformationBlockAddressOrRootGroupObjectHeaderAddress;
        final int rootGroupSymbolTableEntryOrSuperblockChecksum;


        // next bytes are only present in version 0 and 1
        if (version < 2) {
            // only check if the value of the unsigned byte is correct for the Free Space Manager, Root Symbol Table Entry and Shared Header Message
            Preconditions.checkArgument(
                    FREE_SPACE_MANAGER_VERSION_NUMBER == dataInput.readUnsignedByte(),
                    "Invalid value at Free Space Manager Version Number byte");
            Preconditions.checkArgument(
                    ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER == dataInput.readUnsignedByte(),
                    "Invalid value at Root Symbol Table Entry Version Number byte");
            // reserved byte (should be 0) TODO: should we check?
            dataInput.readUnsignedByte();
            Preconditions.checkArgument(
                    SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER == dataInput.readUnsignedByte(),
                    "Invalid value at Shared Header Message Format Version Number byte");
        }
        // next bytes are in the same order for all versions
        sizeOfOffsets = dataInput.readUnsignedByte();
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
        baseAddress = readLittleEndianBigInteger(dataInput, sizeOfOffsets);
        // for version 0 and 1, free space address; for version 2 and 3, superblock extension address
        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress =
                readLittleEndianBigInteger(dataInput, sizeOfOffsets);
        // now it comes the end of file address
        endOfFileAddress = readLittleEndianBigInteger(dataInput, sizeOfOffsets);
        // for version 0 and 1, driver information block address; for version 2 and 3, superblock extension address
        driverInformationBlockAddressOrRootGroupObjectHeaderAddress =
                readLittleEndianBigInteger(dataInput, sizeOfOffsets);

        // read the last bytes of the superblock (integer value)
        rootGroupSymbolTableEntryOrSuperblockChecksum = dataInput.readInt();

        switch (version) {
            case 0:
                new SuperblockVersion0or1.SuperblockVersion0(sizeOfOffsets, sizeOfLengths,
                        fileConsistencyFlags, baseAddress, endOfFileAddress,
                        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress,
                        driverInformationBlockAddressOrRootGroupObjectHeaderAddress, groupLeafNodeK,
                        groupInternalNodeK, rootGroupSymbolTableEntryOrSuperblockChecksum);
            case 1:
                new SuperblockVersion0or1.SuperblockVersion1(sizeOfOffsets, sizeOfLengths,
                        fileConsistencyFlags, baseAddress, endOfFileAddress,
                        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress,
                        driverInformationBlockAddressOrRootGroupObjectHeaderAddress, groupLeafNodeK,
                        groupInternalNodeK, rootGroupSymbolTableEntryOrSuperblockChecksum,
                        indexedStorageInternalNodeK);
            case 2:
                new SuperblockVersion2(sizeOfOffsets, sizeOfLengths, fileConsistencyFlags,
                        baseAddress, endOfFileAddress,
                        addressOfGlobalFreeSpaceIndexOrSuperblockExtensionAddress,
                        driverInformationBlockAddressOrRootGroupObjectHeaderAddress,
                        rootGroupSymbolTableEntryOrSuperblockChecksum);
            default:
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

    /**
     * This value is used to determine the format of the information in the superblock. When the
     * format of the information in the superblock is changed, the version number is incremented to
     * the next integer and can be used to determine how the information in the superblock is
     * formatted.
     *
     * <p>Values of 0, 1 and 2 are defined for this field.
     *
     * <p>This field is present in version 0+ of the superblock.
     */
    public int getVersionNumber() {
        // spans 1 byte, but it is unsigned
        return version;
    }

    /**
     * This value is used to determine the format of the file’s free space information.
     *
     * <p>The only value currently valid in this field is ‘0’, which indicates that the file’s free
     * space is as described below.
     *
     * <p>This field is present in versions 0 and 1 of the superblock.
     */
    public int getFreeSpaceManagerVersionNumber() {
        // TODO: document exception
        throw new UnsupportedOperationException(
                "Free Space Manager Version Number is not present for version " + version);
    }

    /**
     * This value is used to determine the format of the information in the Root Group Symbol Table
     * Entry. When the format of the information in that field is changed, the version number is
     * incremented to the next integer and can be used to determine how the information in the field
     * is formatted.
     *
     * <p>The only value currently valid in this field is ‘0’, which indicates that the root group
     * symbol table entry is formatted as described below.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    public int getRootSymbolTableEntryVersionNumber() {
        // TODO: document exception
        throw new UnsupportedOperationException(
                "Free Space Manager Version Number is not present for version " + version);
    }

    /**
     * This value is used to determine the format of the information in a shared object header
     * message. Since the format of the shared header messages differs from the other private header
     * messages, a version number is used to identify changes in the format.
     *
     * <p>The only value currently valid in this field is ‘0’, which indicates that shared header
     * messages are formatted as described below.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    public int getSharedHeaderMessageFormatVersionNumber() {
        // TODO: document exception
        throw new UnsupportedOperationException(
                "Free Space Manager Version Number is not present for version " + version);
    }

    /**
     * This value contains the number of bytes used to store addresses in the file. The values for
     * the addresses of objects in the file are offsets relative to a base address, usually the
     * address of the superblock signature. This allows a wrapper to be added after the file is
     * created without invalidating the internal offset locations.
     *
     * <p>This field is present in version 0+ of the superblock.
     */
    public int getSizeOfOffsets() {
        // spans 1 byte, but it is unsigned
        return sizeOfOffsets;
    }

    /**
     * This value contains the number of bytes used to store the size of an object.
     *
     * <p>This field is present in version 0+ of the superblock.
     */
    public int getSizeOfLengths() {
        // spans 1 byte, but it is unsigned
        return sizeOfLengths;
    }

    /**
     * Each leaf node of a group B-tree will have at least this many entries but not more than twice
     * this many. If a group has a single leaf node then it may have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    // TODO: add @see description of B-trees
    public int getGroupLeafNodeK() {
        throw new UnsupportedOperationException(
                "Group Leaf Node K is undefined for version " + version);
    }

    /**
     * Each internal node of a group B-tree will have at least this many entries but not more than
     * twice this many. If the group has only one internal node then it might have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    // TODO: add @see description of B-trees
    public int getGroupInternalNodeK() {
        throw new UnsupportedOperationException(
                "Group Leaf Node K is undefined for version " + version);
    }

    /**
     * For superblock version 0, 1 and 2: This field is unused and should be ignored.
     *
     * <p>For superblock version 3: This value contains flags to ensure file consistency for file
     * locking. Currently, the following bit flags are defined:
     *
     * <ul>
     * <li>Bit 0 if set indicates that the file has been opened for write access.</li>
     * <li>Bit 1 is reserved for future use.</li>
     * <li>Bit 2 if set indicates that the file has been opened for single-writer/multiple-reader
     * (SWMR) write access.</li>
     * <li>Bits 3-7 are reserved for future use.</li>
     * </ul>
     *
     * <p>Bit 0 should be set as the first action when a file has been opened for write access. Bit
     * 2 should be set when a file has been opened for SWMR write access. These two bits should be
     * cleared only as the final action when closing a file.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * <p>The size of this field has been reduced from 4 bytes in superblock format versions 0 and 1
     * to 1 byte.
     *
     * @implNote the only version that does not ignore this value has a size of 1 byte, so the
     * return type is byte instead of int.
     */
    public int getFileConsistencyFlags() {
        // TODO: add a warning?
        return fileConsistencyFlags;
    }

    /**
     * Each internal node of an indexed storage B-tree will have at least this many entries but not
     * more than twice this many. If the index storage B-tree has only one internal node then it
     * might have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 1 of the superblock.
     */
    // TODO: add @see description of B-trees
    public int getIndexedStorageInternalNodeK() {
        throw new UnsupportedOperationException(
                "Indexed Storage Internal Node K is undefined for version " + version);
    }

    /**
     * This is the absolute file address of the first byte of the HDF5 data within the file. The
     * library currently constrains this value to be the absolute file address of the superblock
     * itself when creating new files; future versions of the library may provide greater
     * flexibility. When opening an existing file and this address does not match the offset of the
     * superblock, the library assumes that the entire contents of the HDF5 file have been adjusted
     * in the file and adjusts the base address and end of file address to reflect their new
     * positions in the file. Unless otherwise noted, all other file addresses are relative to this
     * base address.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @implNote returns a {@link BigInteger} constructed with a {@code byte[]} with size
     * {@link #getSizeOfOffsets()}.
     */
    public BigInteger getBaseAddress() {
        return baseAddress;
    }

    /**
     * The file’s free space is not persistent for version 0 and 1 of the superblock. Currently this
     * field always contains the undefined address.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @implNote returns a {@link BigInteger} constructed with a {@code byte[]} with size
     * {@link #getSizeOfOffsets()}.
     */
    public BigInteger getAddressOfGlobalFreeSpaceIndex() {
        // TODO: documentation and msg
        throw new UnsupportedOperationException("");
    }

    /**
     * This is the absolute file address of the first byte past the end of all HDF5 data. It is used
     * to determine whether a file has been accidently truncated and as an address where file data
     * allocation can occur if space from the free list is not used.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @implNote returns a {@link BigInteger} constructed with a {@code byte[]} with size
     * {@link #getSizeOfOffsets()}.
     */
    public BigInteger getEndOfFileAddress() {
        return endOfFileAddress;
    }

    /**
     * This is the relative file address of the file driver information block which contains
     * driver-specific information needed to reopen the file. If there is no driver information
     * block then this entry should be the undefined address.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @implNote returns a {@link BigInteger} constructed with a {@code byte[]} with size
     * {@link #getSizeOfOffsets()}.
     */
    public BigInteger getDriverInformationBlockAddress() {
        // TODO: document and msg
        throw new UnsupportedOperationException("");
    }


    /**
     * This is the symbol table entry of the root group, which serves as the entry point into the
     * group graph for the file.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    public int getRootGroupSymbolTableEntry() {
        // TODO: document and msg
        throw new UnsupportedOperationException("");
    }

    /**
     * The field is the address of the object header for the superblock extension. If there is no
     * extension then this entry should be the undefined address.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @implNote returns a {@link BigInteger} constructed with a {@code byte[]} with size
     * {@link #getSizeOfOffsets()}.
     */
    public BigInteger getSuperblockExtensionAddress() {
        // TODO: document and msg
        throw new UnsupportedOperationException("");
    }

    /**
     * This is the address of the root group object header, which serves as the entry point into the
     * group graph for the file.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @implNote returns a long independently of the {@link #getSizeOfOffsets()}.
     */
    public BigInteger getRootGroupObjectHeaderAddress() {
        // TODO: document and msg
        throw new UnsupportedOperationException("");
    }

    /**
     * The checksum for the superblock.
     *
     * <p>This field is present in version 2+ of the superblock.
     */
    public int getSuperblockChecksum() {
        // TODO: document and msg
        throw new UnsupportedOperationException("");
    }
}
