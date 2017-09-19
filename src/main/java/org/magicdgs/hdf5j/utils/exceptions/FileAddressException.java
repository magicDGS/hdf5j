package org.magicdgs.hdf5j.utils.exceptions;

import org.magicdgs.hdf5j.fileformat.FileAddress;

/**
 * Exception class for issues related with {@link FileAddress}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class FileAddressException extends HDF5jException {

    /**
     * Construct a new {@link FileAddressException}.
     *
     * @param msg message for the exception.
     */
    public FileAddressException(final String msg) {
        super(msg);
    }

    /**
     * Construct a new {@link FileAddressException}.
     *
     * @param address file address.
     * @param msg     message for the exception.
     */
    public FileAddressException(final FileAddress address, final String msg) {
        super(formatMessage(address, msg));
    }

    /**
     * Construct a new {@link FileAddressException}.
     *
     * @param address file address.
     * @param msg     message for the exception.
     * @param cause   cause of the exception for the stacktrace.
     */
    public FileAddressException(final FileAddress address, final String msg,
            final Throwable cause) {
        super(formatMessage(address, msg), cause);
    }

    /**
     * Construct a new {@link FileAddressException}.
     *
     * @param address file address.
     * @param cause   cause of the exception for the message and stacktrace.
     */
    public FileAddressException(final FileAddress address, final Throwable cause) {
        super(formatMessage(address, cause.getMessage()), cause);
    }

    // helper method for format the message
    private static final String formatMessage(final FileAddress address, final String msg) {
        return String.format("%s: %s", address.hexDisplay(), msg);
    }
}
