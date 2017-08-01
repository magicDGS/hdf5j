package org.magicdgs.hdf5j.fileformat.level0;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.DataInputStream;

/**
 * Common layout for the Level 0A - Superblock. Note that the Format Signature is not included in
 * this layout, because it is represented by the constant
 * {@link org.magicdgs.hdf5j.HDF5Constants#FORMAT_SIGNATURE}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface Superblock {

    // TODO: set the correct contract for this. Should start from the beginning of the Stream?
    // TODO: maybe read from a Path?
    public Superblock readFromOffset(final LittleEndianDataInputStream stream);

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
    public byte getVersionNumber(); // spans 1 byte

    /**
     * This value is used to determine the format of the file’s free space information.
     *
     * <p>The only value currently valid in this field is ‘0’, which indicates that the file’s free
     * space is as described below.
     *
     * <p>This field is present in versions 0 and 1 of the superblock.
     */
    public byte getFreeSpaceManagerVersionNumber(); // spans 1 byte

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
    public byte getRootSymbolTableEntryVersionNumber(); // spans 1 byte

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
    public byte getSharedHeaderMessageFormat(); // spans 1 byte

    /**
     * This value contains the number of bytes used to store addresses in the file. The values for
     * the addresses of objects in the file are offsets relative to a base address, usually the
     * address of the superblock signature. This allows a wrapper to be added after the file is
     * created without invalidating the internal offset locations.
     *
     * <p>This field is present in version 0+ of the superblock.
     */
    public byte getSizeOfOffsets(); // spans 1 byte

    /**
     * This value contains the number of bytes used to store the size of an object.
     *
     * <p>This field is present in version 0+ of the superblock.
     */
    public byte getSizeOfLengths(); // spans 1 byte

    /**
     * Each leaf node of a group B-tree will have at least this many entries but not more than twice
     * this many. If a group has a single leaf node then it may have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    public short getGroupLeafNodeK(); // span 2 bytes (short)

    /**
     * Each internal node of a group B-tree will have at least this many entries but not more than
     * twice this many. If the group has only one internal node then it might have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    // TODO: add @see description of B-trees
    public short getGroupInternalNodeK(); // span 2 bytes (short)

    /**
     * For superblock version 0, 1 and 3: This field is unused and should be ignored.
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
    public byte getFileConsistencyFlags(); // spans 1 byte for the only used version

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
    public short getIndexedStorageInternalNodeK(); // span 2bytes (short)

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
     * @implNote returns a Number because the length of the fields depends on the {@link #getSizeOfOffsets()}.
     */
    public Number getBaseAddress();

    /**
     * The file’s free space is not persistent for version 0 and 1 of the superblock. Currently this
     * field always contains the undefined address.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @implNote returns a Number because the length of the fields depends on the {@link #getSizeOfOffsets()}.
     */
    public Number getAddressOfGlobalFreeSpaceIndex();

    /**
     * This is the absolute file address of the first byte past the end of all HDF5 data. It is used
     * to determine whether a file has been accidently truncated and as an address where file data
     * allocation can occur if space from the free list is not used.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @implNote returns a Number because the length of the fields depends on the {@link #getSizeOfOffsets()}.
     */
    public Number getEndOfFileAddress();

    /**
     * This is the relative file address of the file driver information block which contains
     * driver-specific information needed to reopen the file. If there is no driver information
     * block then this entry should be the undefined address.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @implNote returns a Number because the length of the fields depends on the {@link #getSizeOfOffsets()}.
     */
    public Number getDriverInformationBlockAddress();


    /**
     * This is the symbol table entry of the root group, which serves as the entry point into the
     * group graph for the file.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     */
    public int getRootGroupSymbolTableEntry();

    /**
     * The field is the address of the object header for the superblock extension. If there is no
     * extension then this entry should be the undefined address.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @implNote returns a Number because the length of the fields depends on the {@link #getSizeOfOffsets()}.
     */
    public Number getSuperblockExtensionAddress(); // spans 4 bytes (int)

    /**
     * This is the address of the root group object header, which serves as the entry point into the
     * group graph for the file.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @implNote returns a Number because the length of the fields depends on the {@link #getSizeOfOffsets()}.
     */
    public Number getRootGroupObjectHeaderAddress();

    /**
     * The checksum for the superblock.
     *
     * <p>This field is present in version 2+ of the superblock.
     */
    public int getSuperblockChecksum(); // spans 4 bytes (int)


}
