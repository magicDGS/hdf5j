package org.magicdgs.hdf5j.fileformat.level0.superblock;

import org.magicdgs.hdf5j.HDF5Constants;
import org.magicdgs.hdf5j.io.FileAddressManager;

import com.google.common.io.LittleEndianDataOutputStream;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Superblock interface for Superbloc. Note that the Format Signature is not included in
 * this layout, because it is represented by the constant {@link HDF5Constants#FORMAT_SIGNATURE}.
 *
 * <p>The superblock may begin at certain predefined offsets within the HDF5 file, allowing a block
 * of unspecified content for users to place additional information at the beginning (and end) of
 * the HDF5 file without limiting the HDF5 Library’s ability to manage the objects within the file
 * itself. This feature was designed to accommodate wrapping an HDF5 file in another file format or
 * adding descriptive information to an HDF5 file without requiring the modification of the actual
 * file’s information. The superblock is located by searching for the HDF5 format signature at byte
 * offset 0, byte offset 512, and at successive locations in the file, each a multiple of two of
 * the previous location; in other words, at these byte offsets: 0, 512, 1024, 2048, and so on.
 *
 * <p>The superblock is composed of the format signature, followed by a superblock version number
 * and information that is specific to each version of the superblock.
 *
 * <p>Currently, there are four versions of the superblock format:
 *
 * <ul>
 *
 * <li>Version 0 is the default format.</li>
 *
 * <li>Version 1 is the same as version 0 but with the “Indexed Storage Internal Node K” field for
 * storing non-default B-tree ‘K’ value.</li>
 *
 * <li>Version 2 has some fields eliminated and compressed from superblock format versions 0 and 1.
 * It has added checksum support and superblock extension to store additional superblock
 * metadata.</li>
 *
 * <li>Version 3 is the same as version 2 except that the field “File Consistency Flags” is used
 * for file locking. This format version will enable support for the latest version.</li>
 *
 * </ul>
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote all byte/short fields returns an {@link int} because they are represented as unsigned
 * bytes.
 */
public interface Superblock {

    /**
     * The only value currently valid in the <b>Version Number of the File’s Free Space
     * Information</b> field is ‘0’, which indicates that the file’s free space is as described in
     * <b>Free-space Manager</b>.
     *
     * @see #getFreeSpaceManagerVersionNumber().
     */
    // TODO: add link to Free-space Manager class once it is implemented
    public static final int FREE_SPACE_MANAGER_VERSION_NUMBER = 0;

    /**
     * The only value currently valid in the <b>Version Number of the Root Group Symbol Table
     * Entry</b> field is ‘0’, which indicates that the root group symbol table entry is formatted
     * as described in <b>Symbol Table Entry</b>.
     *
     * @see #getRootSymbolTableEntryVersionNumber().
     */
    // TODO: add link to Symbol Table Entry class once it is implemented
    public static final int ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER = 0;

    /**
     * The only value currently valid in the <b>Version Number of the Shared Header Message
     * Format</b> field is ‘0’, which indicates that shared header messages are formatted as
     * described in <b>Data Object Header Messages</b>.
     *
     * @see #getSharedHeaderMessageFormatVersionNumber().
     */
    // TODO: add link to Data Object Header Messages class once it is implemented
    public static final int SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER = 0;


    /**
     * Gets the <b>Version Number of the Superblock</b>.
     *
     * <p>This value is used to determine the format of the information in the superblock. When the
     * format of the information in the superblock is changed, the version number is incremented to
     * the next integer and can be used to determine how the information in the superblock is
     * formatted.
     *
     * <p>Values of 0, 1 and 2 are defined for this field.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @return unsigned byte representing the version number of the superblock.
     */
    public int getVersionNumber();


    /**
     * Gets the <b>Version Number of the File’s Free Space Information</b>.
     *
     * <p>This value is used to determine the format of the file’s free space information.
     *
     * <p>The only value currently valid in this field is {@link #FREE_SPACE_MANAGER_VERSION_NUMBER}.
     *
     * <p>This field is present in versions 0 and 1 of the superblock.
     *
     * @return if present, unsigned byte representing the version of the free space manager.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     * @see #FREE_SPACE_MANAGER_VERSION_NUMBER
     */
    // TODO: add link to Free-space Manager class once it is implemented
    public int getFreeSpaceManagerVersionNumber();

    /**
     * Gets the <b>Version Number of the Root Group Symbol Table Entry</b>.
     *
     * <p>This value is used to determine the format of the information in the Root Group Symbol
     * Table Entry. When the format of the information in that field is changed, the version number
     * is incremented to the next integer and can be used to determine how the information in the
     * field is formatted.
     *
     * <p>The only value currently valid in this field is {@link #ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER}.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return if present, unsigned byte representing the version number for the root symbol table entry.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     * @see #ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER
     */
    // TODO: add link to Symbol Table Entry class once it is implemented
    public int getRootSymbolTableEntryVersionNumber();

    /**
     * Gets the <b>Version Number of the Shared Header Message Format</b>.
     *
     * <p>This value is used to determine the format of the information in a shared object header
     * message. Since the format of the shared header messages differs from the other private header
     * messages, a version number is used to identify changes in the format.
     *
     * <p>The only value currently valid in this field is ‘0’, which indicates that shared header
     * messages are formatted as described below.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return if present, unsigned byte representing the version number of the shared header
     * message format.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     * @see #SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER
     */
    // TODO: add link to Data Object Header Messages class once it is implemented
    public int getSharedHeaderMessageFormatVersionNumber();

    /**
     * Gets the <b>Size of Offsets</b>.
     *
     * <p>This value contains the number of bytes used to store addresses in the file. The values
     * for the addresses of objects in the file are offsets relative to a base address, usually the
     * address of the superblock signature. This allows a wrapper to be added after the file is
     * created without invalidating the internal offset locations.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @return unsigned byte representing the number of bytes used to store addressed.
     */
    public int getSizeOfOffsets();

    /**
     * Gets the <b>Size of Lengths</b>.
     *
     * <p>This value contains the number of bytes used to store the size of an object.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @return unsigned byte representing the number of bytes used to store the size of an object.
     */
    public int getSizeOfLengths();

    /**
     * Gets the <b>Group Leaf Node K</b>.
     *
     * <p>Each leaf node of a group B-tree will have at least this many entries but not more than
     * twice this many. If a group has a single leaf node then it may have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return unsigned byte used for compute the minimum and maximum number of entries in a B-Tree group.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add a link to B-Tree class once it is implemented
    public int getGroupLeafNodeK();

    /**
     * Gets the <b>Group Internal Node K</b>.
     *
     * <p>Each internal node of a group B-tree will have at least this many entries but not more
     * than twice this many. If the group has only one internal node then it might have fewer
     * entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return unsigned byte used for compute the minimum and maximum number of entries in a B-Tree
     * internal node.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add a link to B-Tree class once it is implemented
    public int getGroupInternalNodeK();

    /**
     * Gets the <b>File Consistency Flags</b>.
     *
     * <p>For superblock version 0, 1 and 2: This field is unused and should be ignored.
     *
     * <p>For superblock version 3: This value contains flags to ensure file consistency for file
     * locking. Currently, the following bit flags are defined:
     *
     * <ul>
     *
     * <li>Bit 0 if set indicates that the file has been opened for write access.</li>
     *
     * <li>Bit 1 is reserved for future use.</li>
     *
     * <li>Bit 2 if set indicates that the file has been opened for single-writer/multiple-reader
     * (SWMR) write access.</li>
     *
     * <li>Bits 3-7 are reserved for future use.</li>
     *
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
     * @return integer for versions 0 and 1; unsigned byte for versions 2 and 3 containing the
     * consistency flags.
     */
    public int getFileConsistencyFlags();

    /**
     * Gets the <b>Indexed Storage Internal Node K</b>.
     *
     * <p>Each internal node of an indexed storage B-tree will have at least this many entries but n
     * ot more than twice this many. If the index storage B-tree has only one internal node then it
     * might have fewer entries.
     *
     * <p>This value must be greater than zero.
     *
     * <p>This field is present in version 1 of the superblock.
     *
     * @return unsigned byte used for compute the minimum and maximum number of entries in a B-Tree
     * indexed internal node.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add a link to B-Tree class once it is implemented
    public int getIndexedStorageInternalNodeK();

    /**
     * Gets the <b>Base Address</b>.
     *
     * <p>This is the absolute file address of the first byte of the HDF5 data within the file. The
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
     * @return address of the first byte of the HDF5 data.
     *
     * @implNote the return type is a {@link BigInteger} to encapsulate the {@code byte[]} with
     */
    public FileAddressManager.FileAddress getBaseAddress();

    /**
     * Gets the <b>Address of Global Free-space Index</b>.
     *
     * <p>The file’s free space is not persistent for version 0 and 1 of the superblock. Currently
     * this field always contains the undefined address.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return undefined address (file address with all bits set).
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    public FileAddressManager.FileAddress getAddressOfGlobalFreeSpaceIndex();

    /**
     * Gets the <b>End of File Address</b>.
     *
     * <p>This is the absolute file address of the first byte past the end of all HDF5 data. It is
     * used to determine whether a file has been accidently truncated and as an address where file
     * data allocation can occur if space from the free list is not used.
     *
     * <p>This field is present in version 0+ of the superblock.
     *
     * @return address of the first byte past the end of the HDF5 data.
     */
    public FileAddressManager.FileAddress getEndOfFileAddress();

    /**
     * Gets the <b>Driver Information Block Address</b>.
     *
     * <p>This is the relative file address of the file driver information block which contains
     * driver-specific information needed to reopen the file. If there is no driver information
     * block then this entry should be the undefined address.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return address of the file driver information block; undefined address if there is no driver.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add link to File Driver Info class once it is implemented
    public FileAddressManager.FileAddress getDriverInformationBlockAddress();


    /**
     * Gets the <b>Root Group Symbol Table Entry</b>.
     *
     * <p>This is the symbol table entry of the root group, which serves as the entry point into the
     * group graph for the file.
     *
     * <p>This field is present in version 0 and 1 of the superblock.
     *
     * @return the Root Group Symbol Table Entry.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add link to the Symbol Table Entry class once it is implemented
    // TODO: this should return the Symbol Table Entry
    public Object getRootGroupSymbolTableEntry();

    /**
     * Gets the <b>Superblock Extension Address</b>.
     *
     * The field is the address of the object header for the superblock extension. If there is no
     * extension then this entry should be the undefined address.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add link to the Superblock Extension Address class once it is implemented
    public FileAddressManager.FileAddress getSuperblockExtensionAddress();

    /**
     * Gets the <b>Root Group Object Header Address</b>-
     *
     * This is the address of the root group object header, which serves as the entry point into the
     * group graph for the file.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @return address of the root group object header.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: add link to the Root Group Object Header class
    public FileAddressManager.FileAddress getRootGroupObjectHeaderAddress();

    /**
     * Gets the <b>Superblock Checksum</b>.
     *
     * <p>The checksum for the superblock.
     *
     * <p>This field is present in version 2+ of the superblock.
     *
     * @return integer representing the checksum.
     *
     * @throws UnsupportedOperationException TODO: change for a SuperblockVersionException
     */
    // TODO: document that it should be computed using the Jenkins’ lookup3 algorithm
    public int getSuperblockChecksum();


    /**
     * Writes the superblock to the output stream (little-endian).
     *
     * @param littleEndianDataOutputStream output stream.
     *
     * @throws IOException if an IO error occurs.
     */
    public void write(final LittleEndianDataOutputStream littleEndianDataOutputStream)
            throws IOException;

}
