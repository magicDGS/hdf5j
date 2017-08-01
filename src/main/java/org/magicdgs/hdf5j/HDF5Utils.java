package org.magicdgs.hdf5j;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Utilities for HDF5 files.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class HDF5Utils {

    // cannot be instantiated
    private HDF5Utils() {}

    /**
     * Check if the beginning of an {@link InputStream} is in the HDF5 format; that is, starts with
     * the {@link HDF5Constants#FORMAT_SIGNATURE}.
     *
     * @param inputStream input stream with mark support.
     *
     * @return {@code true} if input stream is an HDF5 formatted; {@code false} otherwise.
     *
     * @throws IllegalArgumentException if mark is not supported for the stream.
     * @throws IOException              if an IO error occurs.
     */
    public static boolean isHDF5Stream(final InputStream inputStream) throws IOException {
        Preconditions.checkNotNull(inputStream, "null inputStream");
        Preconditions.checkArgument(inputStream.markSupported(),
                "cannot check an inputStream without mark support: %s", inputStream);
        // mark the stream to come back
        inputStream.mark(HDF5Constants.FORMAT_SIGNATURE.length);
        boolean isHDF5 = checkHDF5signature(inputStream);
        // reset the stream
        inputStream.reset();
        return isHDF5;
    }

    /**
     * Check if the start of the file is an HDF5 formatted file; that is, it starts with the
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
            return checkHDF5signature(is);
        }
    }

    // helper method which consume the InputStream for avoid the usage of mark if it isn't necessary
    private static boolean checkHDF5signature(final InputStream inputStream) throws IOException {
        // get the possible HDF5 signature
        final byte[] bytesSignature = new byte[HDF5Constants.FORMAT_SIGNATURE.length];
        final int bytesRead = inputStream.read(bytesSignature);
        // early termination if less than 8 bytes were read
        if (bytesRead != HDF5Constants.FORMAT_SIGNATURE.length) {
            return false;
        }
        // otherwise, convert the bytes to unsigned int and compare to the signature
        final int[] possibleSignature = new int[bytesSignature.length];
        for (int i = 0; i < bytesSignature.length; i++) {
            possibleSignature[i] = Byte.toUnsignedInt(bytesSignature[i]);
        }
        // compare the signature
        return Arrays.equals(possibleSignature, HDF5Constants.FORMAT_SIGNATURE);
    }

}
