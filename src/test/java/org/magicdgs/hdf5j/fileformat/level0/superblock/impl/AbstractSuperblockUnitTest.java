package org.magicdgs.hdf5j.fileformat.level0.superblock.impl;

import org.magicdgs.hdf5j.HDF5Constants;
import org.magicdgs.hdf5j.HDF5jTest;
import org.magicdgs.hdf5j.utils.HDF5Utils;
import org.magicdgs.hdf5j.utils.HDF5jException;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class AbstractSuperblockUnitTest extends HDF5jTest {

    @Test
    public void testGetSignatureBufferToWrite() throws Exception {
        final ByteBuffer buffer = AbstractSuperblock.getSignatureBufferToWrite();
        // check if it can be written (to a temp file)
        final Path tmpFile = Files.createTempFile("testGetSignatureBufferToWrite", ".h5");
        try (final SeekableByteChannel sbc =
                Files.newByteChannel(tmpFile, StandardOpenOption.WRITE)) {
            sbc.write(buffer);
            // assess that now the file has the size of the signature
            Assert.assertEquals(sbc.size(), HDF5Constants.FORMAT_SIGNATURE.length);
        }

        // now we should check that the signature is correct (for our library at least)
        Assert.assertTrue(HDF5Utils.isHDF5File(tmpFile));
        // remove the temp file explicitly
        Files.delete(tmpFile);
    }

    @Test
    public void testVersionSpecificMethodsThrowException() throws Exception {
        // using mockito to call the real methods
        final AbstractSuperblock impl = Mockito.mock(AbstractSuperblock.class, Mockito.CALLS_REAL_METHODS);

        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getFreeSpaceManagerVersionNumber);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getRootSymbolTableEntryVersionNumber);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getSharedHeaderMessageFormatVersionNumber);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getGroupLeafNodeK);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getGroupInternalNodeK);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getIndexedStorageInternalNodeK);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getAddressOfGlobalFreeSpaceIndex);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getDriverInformationBlockAddress);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getRootGroupSymbolTableEntry);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getSuperblockExtensionAddress);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getRootGroupObjectHeaderAddress);
        Assert.assertThrows(HDF5jException.SuperblockVersionException.class, impl::getSuperblockChecksum);

    }

}