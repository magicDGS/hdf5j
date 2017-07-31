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
    public static final int[] FORMAT_SIGNATURE = "\211HDF\r\n\032\n".chars().toArray();

    // cannot be instantiated
    private HDF5Constants() {}

}
