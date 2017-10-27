package org.magicdgs.hdf5j.utils.exceptions;

import org.magicdgs.hdf5j.fileformat.FileAddress;
import org.magicdgs.hdf5j.fileformat.ObjectSize;

/**
 * Exception class for issues related with {@link ObjectSize}.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class ObjectSizeException extends HDF5jException {

    /**
     * Construct a new {@link ObjectSizeException}.
     *
     * @param msg message for the exception.
     */
    public ObjectSizeException(final String msg) {
        super(msg);
    }

    /**
     * Construct a new {@link ObjectSizeException}.
     *
     * @param objectSize object size.
     * @param msg        message for the exception.
     */
    public ObjectSizeException(final ObjectSize objectSize, final String msg) {
        super(formatMessage(objectSize, msg));
    }

    /**
     * Construct a new {@link ObjectSizeException}.
     *
     * @param objectSize object size.
     * @param msg        message for the exception.
     * @param cause      cause of the exception for the stacktrace.
     */
    public ObjectSizeException(final ObjectSize objectSize, final String msg,
            final Throwable cause) {
        super(formatMessage(objectSize, msg), cause);
    }

    /**
     * Construct a new {@link ObjectSizeException}.
     *
     * @param objectSize object size.
     * @param cause      cause of the exception for the message and stacktrace.
     */
    public ObjectSizeException(final ObjectSize objectSize, final Throwable cause) {
        super(formatMessage(objectSize, cause.getMessage()), cause);
    }

    // helper method for format the message
    private static final String formatMessage(final ObjectSize objectSize, final String msg) {
        return String.format("%s: %s", objectSize.getObjectLength(), msg);
    }
}
