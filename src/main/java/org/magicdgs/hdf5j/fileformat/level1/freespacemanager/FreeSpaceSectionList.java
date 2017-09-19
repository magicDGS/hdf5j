package org.magicdgs.hdf5j.fileformat.level1.freespacemanager;

import org.magicdgs.hdf5j.fileformat.HDF5FileBlock;
import org.magicdgs.hdf5j.fileformat.address.FileAddress;
import org.magicdgs.hdf5j.fileformat.level0.Superblock;
import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord.SectionDataRecord;

/**
 * Free-space Section List  interface (level 1H). Note that the Format Signature is a constant
 * represented by {@link #SIGNARUTE}.
 *
 * <p>Free-space managers are used to describe space within a heap or the entire HDF5 file that is
 * not currently used for that heap or file.
 *
 * <p>The free-space section list stores a collection of free-space sections that is specific to
 * each client of the free-space manager. For example, the fractal heap is a client of the free
 * space manager and uses it to track unused space within the heap. There are 4 types of section
 * records for the fractal heap, each of which has its own format, listed below.
 *
 * <p>The free-space sections being managed are stored in a free-space section list, described
 * below. The sections in the free-space section list are stored in the following way: a count of
 * the number of sections describing a particular size of free space and the size of the free-space
 * described (in bytes), followed by a list of section description records; then another section
 * count and size, followed by the list of section descriptions for that size; and so on.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote all byte/short fields returns an {@link int} because they are represented as unsigned
 * bytes.
 * @implNote all fields where the length depends on the {@link Superblock#getSizeOfLengths()}
 * returns a {@link long}.
 * @see FreeSpaceManagerHeader
 */
public interface FreeSpaceSectionList extends HDF5FileBlock {

    /**
     * Unsigned byte signature for the Free-space Manager Header.
     *
     * @implNote because in Java bytes are always signed, this signature is represented as an array
     * of unsigned integers.
     */
    public static final int[] SIGNARUTE = "FSSE".chars().toArray();

    /**
     * Gets the <b>Free-space Manager Header Address</b>
     *
     * <p>This is the address of the Free-space Manager Header. This field is principally used for
     * file integrity checking.
     *
     * @return address for the Free-space Manager Header.
     *
     * @see FreeSpaceManagerHeader
     */
    public FileAddress getFreeSpaceManagerHeaderAddress();

    /**
     * Gets the <b>Number of Section Records for Set #N</b>
     *
     * <p>This is the number of free-space section records for set #N. The length of this field is
     * the minimum number of bytes needed to store the number of serialized sections (from the
     * free-space manager header).
     *
     * <p>The number of sets of free-space section records is determined by the size of serialized
     * section list in the free-space manager header.
     *
     * @param N set number (0-indexed).
     *
     * @return integer representation of the number of free-space section records for the requested
     * set number.
     *
     * @see FreeSpaceManagerHeader#getSizeOfSerializedSectionListUsed()
     */
    // TODO - maybe it should return a long
    public int getNumberOfSectionRecordsForSet(final int N);

    /**
     * Gets the <b>Section Size for Record Set #N</b>.
     *
     * <p>This is the size (in bytes) of the free-space section described for all the section
     * records in set #N.
     *
     * <p>The length of this field is the minimum number of bytes needed to store the maximum
     * section size (from the free-space manager header).
     *
     * @param N record set number (0-indexed).
     *
     * @return integer representation of the size (in bytes) for all the section records for the
     * requested set number.
     */
    // TODO - maybe it should return a long
    public int getSectionSizeForRecordSet(final int N);

    /**
     * Gets the <b>Record Set #N Section #K Offset</b>.
     *
     * <p>This is the offset (in bytes) of the free-space section within the client for the
     * free-space manager.
     *
     * <p>The length of this field is the minimum number of bytes needed to store the size of
     * address space (from the free-space manager header).
     *
     * @param N record set number (0-indexed).
     * @param K record set section (0-indexed).
     *
     * @return integer representation of the offset (in bytes) for the requested section and set
     * number.
     */
    // TODO - maybe it should return a long
    public int getRecordSetOffset(final int N, final int K);

    /**
     * Gets the <b>Record Set #N Section #K Type</b>.
     *
     * <p>This is the type of the section record, used to decode the record set #N section #K data
     * information. The defined record type for file client is:
     *
     * <table>
     * <tr><th>Type</th><th>Description</th></tr>
     * <tr><td>0</td><td>File’s section (a range of actual bytes in file)</td></tr>
     * <tr><td>1+</td><td>Reserved.</td></tr>
     *
     * </table>
     *
     *
     * <p>The defined record types for a fractal heap client are:
     *
     * <table>
     * <tr><th>Type</th><th>Description</th></tr>
     * <tr><td>0</td><td>Fractal heap “single” section</td></tr>
     * <tr><td>1</td><td>Fractal heap “first row” section</td></tr>
     * <tr><td>2</td><td>Fractal heap “normal row” section</td></tr>
     * <tr><td>3</td><td>Fractal heap “indirect” section</td></tr>
     * <tr><td>4+</td><td>Reserved.</td></tr>
     * </table>
     *
     * @param N record set number (0-indexed).
     * @param K record set section (0-indexed).
     *
     * @return unsigned byte representation
     * @see SectionDataRecord
     */
    // TODO - maybe it should return an class name
    public int getRecordSetType(final int N, final int K);


    /**
     * TODO - documentation
     *
     * @param N record set number (0-indexed).
     * @param K record set section (0-indexed).
     *
     * @return TODO - documentation
     */
    // TODO: change signature
    public SectionDataRecord getRecordSetData(final int N, final int K);
}
