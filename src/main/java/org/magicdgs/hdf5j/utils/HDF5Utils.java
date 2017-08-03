package org.magicdgs.hdf5j.utils;

import org.magicdgs.hdf5j.HDF5Constants;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Utilities for HDF5 files.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class HDF5Utils {

    // cannot be instantiated
    private HDF5Utils() {}

    /**
     * Checks if the beginning of an {@link InputStream} is in the HDF5 format; that is, starts with
     * the {@link HDF5Constants#FORMAT_SIGNATURE}.
     *
     * @param inputStream input stream with mark support.
     *
     * @return {@code true} if input stream is an HDF5 formatted; {@code false} otherwise.
     *
     * @throws NullPointerException     if the input stream is {@code null}.
     * @throws IllegalArgumentException if mark is not supported for the input stream.
     * @throws IOException              if an IO error occurs.
     */
    public static boolean isHDF5Stream(final InputStream inputStream) throws IOException {
        Preconditions.checkNotNull(inputStream, "null inputStream");
        Preconditions.checkArgument(inputStream.markSupported(),
                "cannot check an inputStream without mark support: %s", inputStream);
        // mark the stream to come back
        inputStream.mark(HDF5Constants.FORMAT_SIGNATURE.length);
        boolean isHDF5 = readHDF5signature(inputStream);
        // reset the stream
        inputStream.reset();
        return isHDF5;
    }

    /**
     * Checks if the start of the file is an HDF5 formatted file; that is, it starts with the
     * {@link HDF5Constants#FORMAT_SIGNATURE}.
     *
     * @param path file to check.
     *
     * @return {@code true} if the file is HDF5 formatted; {@code false} otherwise.
     *
     * @throws IOException if an IO error occurs.
     */
    public static boolean isHDF5File(final Path path) throws IOException {
        Preconditions.checkNotNull(path, "null path");
        // auto-closing with try-with-resources
        try (final InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            // does not require mark
            return readHDF5signature(is);
        }
    }

    // helper method which consume the InputStream for avoid the usage of mark if it isn't necessary
    private static boolean readHDF5signature(final InputStream inputStream) throws IOException {
        // get the possible HDF5 signature
        for (int i = 0; i < HDF5Constants.FORMAT_SIGNATURE.length; i++) {
            final int currentByte = inputStream.read();
            if (currentByte == -1 || currentByte != HDF5Constants.FORMAT_SIGNATURE[i]) {
                return false;
            }
        }
        return true;
    }

}
