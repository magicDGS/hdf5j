package org.magicdgs.hdf5j.utils.exceptions;

/**
 * Marker class for exceptions caused by the HDF5j library.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class HDF5jException extends RuntimeException {

    /**
     * Construct a new {@link HDF5jException} with the provided message.
     *
     * @param message message for the exception.
     */
    public HDF5jException(final String message) {
        super(message);
    }

    /**
     * Construct a new {@link HDF5jException} with the provided message, specifying the cause.
     *
     * @param message message for the exception.
     * @param cause   cause of the exception for the stacktrace.
     */
    public HDF5jException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
