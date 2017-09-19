package org.magicdgs.hdf5j.fileformat.level1.freespacemanager;

import org.magicdgs.hdf5j.fileformat.HDF5FileBlock;
import org.magicdgs.hdf5j.fileformat.address.FileAddress;
import org.magicdgs.hdf5j.fileformat.level0.Superblock;
import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord.FileSectionDataRecord;
import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord.FractalHeapSectionDataRecord;

/**
 * Free-space Manager Header interface (level 1H). Note that the Format Signature is a constant
 * represented by {@link #SIGNARUTE}.
 *
 * <p>Free-space managers are used to describe space within a heap or the entire HDF5 file that is
 * not currently used for that heap or file.
 *
 * <p>The free-space manager header contains metadata information about the space being tracked,
 * along with the address of the list of free space sections which actually describes the free
 * space. The header records information about free-space sections being tracked, creation
 * parameters for handling free-space sections of a client, and section information used to locate
 * the collection of free-space sections.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @implNote all byte/short fields returns an {@link int} because they are represented as unsigned
 * bytes.
 * @implNote all fields where the length depends on the {@link Superblock#getSizeOfLengths()}
 * returns a {@link long}.
 * @see FreeSpaceSectionList
 */
public interface FreeSpaceManagerHeader extends HDF5FileBlock {

    /**
     * Unsigned byte signature for the Free-space Manager Header.
     *
     * @implNote because in Java bytes are always signed, this signature is represented as an array
     * of unsigned integers.
     */
    public static final int[] SIGNARUTE = "FSHD".chars().toArray();

    /**
     * Gets the <b>Client ID</b>.
     *
     * <p>This is the client ID for identifying the user of this free-space manager:
     *
     * <table>
     * <tr><th>ID</th><th>Description</th></tr>
     * <tr><td>0</td><td>Fractal heap</td></tr>
     * <tr><td>1</td><td>File</td></tr>
     * <tr><td>2+</td><td>Reserved</td></tr>
     * </table>
     *
     * @return unsigned byte representing the client ID.
     * @see FileSectionDataRecord#CLIENT_ID
     * @see FractalHeapSectionDataRecord
     */
    public int getClientID();

    /**
     * Gets the <b>Total Space Tracked</b>.
     *
     * <p>This is the total amount of free space being tracked, in bytes.
     *
     * @return long representation of total space tracked.
     */
    public long getTotalSpaceTracked();

    /**
     * Gets the <b>Total Number of Sections</b>.
     *
     * <p>This is the total number of free-space sections being tracked.
     *
     * @return long representation of total number of sections.
     */
    public long getTottalNumberOfSections();

    /**
     * Gets the <b>Number of Serialized Sections</b>.
     *
     * <p>This is the number of serialized free-space sections being tracked.
     *
     * @return number of serialized sections (as a long value).
     */
    public long getNumberOfSerializedSections();

    /**
     * Gets the <b>Number of Un-Serialized Sections</b>.
     *
     * <p>This is the number of un-serialized free-space sections being managed. Un-serialized
     * sections are created by the free-space client when the list of sections is read in.
     *
     * @return long representation of number of un-serialized sections.
     */
    public long getNumberOfUnSerializedSections();

    /**
     * Gets the <b>Number of Section Classes</b>.
     *
     * <p>This is the number of section classes handled by this free space manager for the
     * free-space client.
     *
     * @return unsigned short representing the number of section classes.
     */
    public int getNumberOfSectionClasses();

    /**
     * Gets <b>Shrink Percent</b>.
     *
     * <p>This is the percent of current size to shrink the allocated serialized free-space section
     * list.
     *
     * @return unsigned short representing the shrink percent.
     */
    public int getShrinkPercent();

    /**
     * Gets the <b>Expand Percent</b>
     *
     * <p>This is the percent of current size to expand the allocated serialized free-space section
     * list.
     *
     * @return unsigned short representing the expand percent.
     */
    public int getExpandPercent();

    /**
     * Gets the <b>Size of Address Space</b>.
     *
     * <p>This is the size of the address space that free-space sections are within. This is stored
     * as the log2 of the actual value (in other words, the number of bits required to store values
     * within that address space).
     *
     * @return unsigned short representing the log2 size of address space.
     */
    public int getSizeOfAddressSpace();

    /**
     * Gets the <b>Maximum Section Size</b>
     *
     * <p>This is the maximum size of a section to be tracked.
     *
     * @return long representation of the maximum section size.
     */
    public long getMaximumSectionSize();


    /**
     * Gets the <b>Address of Serialized Section List</b>.
     *
     * <p>This is the address where the serialized free-space section list is stored.
     *
     * @return address of serialized section list.
     *
     * @see FreeSpaceSectionList
     */
    public FileAddress getAddressOfSerializedSectionList();

    /**
     * Gets the <b>Size of Serialized Section List Used</b>
     *
     * <p>This is the size of the serialized free-space section list used (in bytes). This value
     * must be less than or equal to the allocated size of serialized section list, below.
     *
     * @return long representation of the size of serialized section list used.
     */
    public long getSizeOfSerializedSectionListUsed();

    /**
     * Gets the <b>Allocated Size of Serialized Section List</b>.
     *
     * <p>This is the size of serialized free-space section list actually allocated (in bytes).
     *
     * @return long representation of the allocated size of serialized section list.
     */
    public long getAllocatedSizeOfSerializedSectionList();

    /**
     * Gets the <b>Checksum</b>. This checksum is computed using the Jenkins’ lookup3
     * algorithm (implemented in {@link org.magicdgs.hdf5j.utils.hashing.Lookup3HashFunction}).
     *
     * <p>This is the checksum for the free-space manager header.
     *
     * @return integer representing the checksum.
     */
    public int getChecksum();
}
