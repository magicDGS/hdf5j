package org.magicdgs.hdf5j;

/**
 * HDF5 format constants.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class HDF5Constants {

    /**
     * Unsigned byte signature for HDF5.
     *
     * <p>Note: because in Java bytes are always signed, this signature is represented as an array
     * of unsigned integers.
     */
    public static final int[] HDF5_FORMAT_SIGNATURE = "\211HDF\r\n\032\n".chars().toArray();

    /**
     * The only value currently valid in the <b>Version Number of the File’s Free Space
     * Information</b> field is ‘0’, which indicates that the file’s free space is as described in
     * <b>Free-space Manager</b>.
     *
     * @see org.magicdgs.hdf5j.fileformat.level0.Superblock#getFreeSpaceManagerVersionNumber()
     * @see org.magicdgs.hdf5j.fileformat.level1.freespacemanager.FreeSpaceManagerHeader
     */
    public static final int FREE_SPACE_MANAGER_VERSION_NUMBER = 0;

    /**
     * The only value currently valid in the <b>Version Number of the Root Group Symbol Table
     * Entry</b> field is ‘0’, which indicates that the root group symbol table entry is formatted
     * as described in <b>Symbol Table Entry</b>.
     *
     * @see org.magicdgs.hdf5j.fileformat.level0.Superblock#getRootSymbolTableEntryVersionNumber()
     */
    // TODO: add link to Symbol Table Entry class once it is implemented
    public static final int ROOT_SYMBOL_TABLE_ENTRY_VERSION_NUMBER = 0;

    /**
     * The only value currently valid in the <b>Version Number of the Shared Header Message
     * Format</b> field is ‘0’, which indicates that shared header messages are formatted as
     * described in <b>Data Object Header Messages</b>.
     *
     * @see org.magicdgs.hdf5j.fileformat.level0.Superblock#getSharedHeaderMessageFormatVersionNumber()
     */
    // TODO: add link to Data Object Header Messages class once it is implemented
    public static final int SHARED_HEADER_MESSAGE_FORMAT_VERSION_NUMBER = 0;

    // cannot be instantiated
    private HDF5Constants() {}

}
