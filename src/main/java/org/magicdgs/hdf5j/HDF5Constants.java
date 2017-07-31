package org.magicdgs.hdf5j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * HDF5 format constants.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class HDF5Constants {

    // cannot be instantiated
    private HDF5Constants() {}

    /** Default charset for HDF5 files (ASCII). */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    /**
     * Unsigned byte signature for HDF5.
     *
     * <p>Note: because in Java bytes are always signed, this signature is represented as an array
     * of unsigned integers.
     */
    public static final int[] FORMAT_SIGNATURE = {137, 72, 68, 70, 13, 10, 26, 10};

}
