package org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord;

import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.FreeSpaceManagerHeader;

/**
 * Marker interface for all Fractal Heap Data Record:
 *
 * <ul>
 *     <li>Fractal Heap “Single” Section Data Record</li>
 *     <li>Fractal Heap “First Row” Section Data Record</li>
 *     <li>Fractal Heap “Normal Row” Section Data Record</li>
 *     <li>Fractal Heap “Indirect” Section Data Record</li>
 * </ul>
 *
 * <p>Note that this excludes the File’s Section Data Record.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface FractalHeapSectionDataRecord extends SectionDataRecord {

    /** Client ID value for {@link FreeSpaceManagerHeader#getClientID()}. */
    public static final int CLIENT_ID = 0;

    /** Returns {@link #CLIENT_ID}. */
    @Override
    default public int getClientID() {
        return CLIENT_ID;
    }

}
