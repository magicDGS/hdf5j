package org.magicdgs.hdf5j.utils.exceptions;

import org.magicdgs.hdf5j.fileformat.level0.Superblock;

/**
 * Exception class for issues related with the Superblock version.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 * @see Superblock
 */
public class SuperblockVersionException extends HDF5jException {

    /**
     * Construct a new {@link SuperblockVersionException}.
     *
     * @param superblock superblock throwing the exception.
     * @param msg        message for the exception.
     * @param cause      cause for the exception.
     */
    public SuperblockVersionException(final Superblock superblock, final String msg,
            final Throwable cause) {
        super(formatMessage(superblock, msg), cause);
    }

    /**
     * Construct a new {@link SuperblockVersionException}.
     *
     * @param superblock superblock throwing the exception.
     * @param msg        message for the exception.
     */
    public SuperblockVersionException(final Superblock superblock, final String msg) {
        super(formatMessage(superblock, msg));
    }

    /**
     * Construct a new {@link SuperblockVersionException}.
     *
     * @param superblock superblock throwing the exception.
     * @param cause         cause of the exception for the message and stacktrace.
     */
    public SuperblockVersionException(final Superblock superblock, final Throwable cause) {
        super(formatMessage(superblock, cause.getMessage()), cause);
    }

    // formats teh message for the superblock exception
    private static final String formatMessage(final Superblock superblock, final String msg) {
        return (String.format("Superblock version %s: %s", superblock.getVersionNumber(), msg));
    }
}
