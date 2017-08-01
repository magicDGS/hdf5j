package org.magicdgs.hdf5j;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public class HDF5UtilsTest extends HDF5jTest {

    // temp directory for creating files
    private Path tmpDir;


    @BeforeClass
    public void setUp() throws IOException {
        tmpDir = Files.createTempDirectory(this.getClass().getName());
    }

    @Test(enabled = false)
    public void testIsHDF5Stream() throws Exception {

    }

    @DataProvider
    public Iterator<Object[]> testFiles() throws IOException {
        final List<Object[]> data = new ArrayList<>();
        // add all the examples by API, that are real HDF5
        HDF5jTestResources.getExamplesByApi().forEach(p -> data.add(new Object[] {p, true}));
        // add empty file to the dataset (no HDF5)
        data.add(new Object[] {Files.createFile(tmpDir.resolve("empty.h5")), false});
        // text file with more than 8 bytes (and CR-LF spacing)
        data.add(new Object[] {
                createTempFileWithContent("text.h5", "HDF5\r\nThis is a text file\r\n"), false});
        // corrupted HDF5 file (no end of line)
        data.add(
                new Object[] {createTempFileWithContent("corrupted.h5", "\211HDF\r\n\032Corrupted"),
                        false});
        // short file (less than 8 bytes, but first bytes the same as HDF5 signature
        data.add(new Object[] {createTempFileWithContent("7bytes.h5", "\211HDF\r\n\032"), false});
        return data.iterator();

    }

    private final Path createTempFileWithContent(final String name, final String content)
            throws IOException {
        final Path path = Files.createFile(tmpDir.resolve(name));
        try (final Writer os = Files.newBufferedWriter(path)) {
            os.write(content);
        }
        return path;
    }

    @Test(dataProvider = "testFiles")
    public void testIsHDF5File(final Path path, final boolean isHDF5) throws Exception {
        Assert.assertEquals(HDF5Utils.isHDF5File(path), isHDF5);
    }

}