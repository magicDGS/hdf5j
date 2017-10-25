package org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord;

import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.FreeSpaceSectionList;

/**
 * Marker interface for <b>Fractal Heap “Single” Section Data Record<</b>.
 *
 * <p>No additional record data stored.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface FractalHeapSingleSectionDataRecord extends FractalHeapSectionDataRecord {

    /** Type value for {@link FreeSpaceSectionList#getRecordSetType(int, int)}. */
    public static final int TYPE = 0;

    /** Returns {@link #TYPE}. */
    @Override
    default public int getType() {
        return TYPE;
    }
}
