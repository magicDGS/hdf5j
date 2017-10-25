package org.magicdgs.hdf5j.fileformat.level1.freespacemanager.sectiondatarecord;

/**
 * Marker interface for Section Data Record:
 *
 * <ul>
 *     <li>File’s Section Data Record</li>
 *     <li>Fractal Heap “Single” Section Data Record</li>
 *     <li>Fractal Heap “First Row” Section Data Record</li>
 *     <li>Fractal Heap “Normal Row” Section Data Record</li>
 *     <li>Fractal Heap “Indirect” Section Data Record</li>
 *     <li>Fractal Heap “Indirect” Section Data Record</li>
 * </ul>
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface SectionDataRecord {

    // TODO - it is really necessary?
    public int getType();

    // TODO - it is really necessary?
    public int getClientID();
}
