package org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord;

import org.magicdgs.hdf5j.fileformat.level1.freespacemanager.FreeSpaceSectionList;

/**
 * Marker interface for <b>Fractal Heap “First Row” Section Data Record<</b>.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface FractalHeapFirstRowSectionDataRecord extends FractalHeapSectionDataRecord {

    /** Type value for {@link FreeSpaceSectionList#getRecordSetType(int, int)}. */
    public static final int TYPE = 1;

    /**
     * Gets the <b>Fractal Heap Block Offset</b>.
     *
     * <p>The offset of the indirect block in the fractal heap’s address space containing the empty
     * blocks.
     *
     * <p>The number of bytes used to encode this field is the minimum number of bytes needed to
     * encode values for the Maximum Heap Size (in the fractal heap’s header).
     *
     * @return long representation of the fractal heap block offset.
     */
    public long getFractalHeapBlockOffset();

    /**
     * Gets the <b>Block Start Row</b>.
     *
     * <p>This is the row that the empty blocks start in.
     *
     * @return unsigned short representing the block start row.
     */
    public int getBlockStartRow();

    /**
     * Gets the <b>Block Start Column</b>.
     *
     * <p>This is the column that the empty blocks start in.
     *
     * @return unsigned short representing the block start column.
     */
    public int getBlockStartColumn();


    /**
     * Gets the <b>Number of Blocks</b>.
     *
     * <p>This is the number of empty blocks covered by the section.
     *
     * @return unsigned short representing the number of blocks.
     */
    public int getNumberOfBlocks();
}
