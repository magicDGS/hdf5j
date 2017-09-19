package org.magicdgs.hdf5j.fileformat;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

/**
 * Interface for a HDF5 block of low-level data.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public interface HDF5FileBlock {

    /**
     * Gets the Version Number of the block.
     *
     * <p>This value is used to determine the format of the information in the block. When the
     * format of the information in the block is changed, the version number is incremented to the
     * next integer and can be used to determine how the information is formatted.
     *
     * @return unsigned byte representing the version number of the block.
     *
     * @implNote all byte/short fields returns an {@link int} because they are represented as
     * unsigned bytes.
     */
    public int getVersionNumber();

    /**
     * Gets the block size in bytes. This correspond to the number of bytes required to write the
     * block in the disk.
     *
     * <p>Note that the block-size might vary for different versions of the block or the value of
     * some fields.
     *
     * @return size of the block (in bytes).
     *
     * @implNote this method should return the number of bytes to be written with the
     * {@link #write(SeekableByteChannel)} method.
     */
    public int getBlockByteSize();

    /**
     * Writes the block to the current position of the {@link SeekableByteChannel}.
     *
     * @param byteChannel channel for writing the block.
     *
     * @throws IOException if an IO error occurs.
     * @implSpec the content of the block should be written in little-endian order as defined in
     * the <a href="https://support.hdfgroup.org/HDF5/doc/H5.format.html">
     * HDF5 File Format Specification Version 3.0</a>
     * @implNote the number of bytes written to the channel should be equal to the value returned by
     * {@link #getBlockByteSize()}.
     */
    public void write(final SeekableByteChannel byteChannel) throws IOException;

}
