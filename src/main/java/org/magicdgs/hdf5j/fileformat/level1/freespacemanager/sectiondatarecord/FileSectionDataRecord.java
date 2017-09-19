package org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord;

import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.FreeSpaceManagerHeader;
import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.FreeSpaceSectionList;

/**
 * Marker interface for <b>File’s Section Data Record</b>.
 *
 * <p>File’s section (a range of actual bytes in file).
 *
 * <p>No additional record data stored.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface FileSectionDataRecord extends SectionDataRecord {

    /** Client ID value for {@link FreeSpaceManagerHeader#getClientID()}. */
    public static final int CLIENT_ID = 1;

    /** Type value for {@link FreeSpaceSectionList#getRecordSetType(int, int)}. */
    public static final int TYPE = 1;

}
