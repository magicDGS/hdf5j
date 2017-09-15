package org.magicdgs.hdf5j.utils;

import org.magicdgs.hdf5j.fileformat.level0.Superblock;
import org.magicdgs.hdf5j.fileformat.address.FileAddress;

/**
 * Marker interface for exceptions caused by the HDF5j library.
 *
 * <p>Contains static classes which extends the exception.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class HDF5jException extends RuntimeException {

    public HDF5jException(final String message) {
        super(message);
    }

    /** Exception class for issues related with {@link FileAddress}. */
    public static class FileAddressException extends HDF5jException {

        public FileAddressException(final String msg) {
            super(msg);
        }

        public FileAddressException(final FileAddress address, final String msg) {
            this(String.format("%s %s", address, msg));
        }
    }

    /**
     * Exception class for issues related with the Superblock version.
     *
     * @see Superblock
     */
    public static class SuperblockVersionException extends HDF5jException {

        public SuperblockVersionException(final Superblock superblock, String msg) {
            super(String.format("Superblock version %s: %s", superblock.getVersionNumber(), msg));
        }
    }

}
