package org.magicdgs.hdf5j;

import org.magicdgs.hdf5j.utils.HDF5Utils;

import com.google.common.primitives.Bytes;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.collections.Ints;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
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

    @AfterClass
    public void tearDown() throws IOException {
        // clean the temp directory
        Files.walk(tmpDir)
                // directories last
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        // rethrow as a runtime exception
                        throw new RuntimeException(e);
                    }
                });
    }

    @DataProvider
    public Object[][] invalidStreams() {
        return new Object[][] {
                // null stream should throw
                {null, NullPointerException.class},
                // also stream without mark support (default for InputStream abstract class)
                {new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                }, IllegalArgumentException.class}
        };
    }

    @Test(dataProvider = "invalidStreams")
    public void testIsHDF5StreamInvalid(final InputStream invalidStream,
            final Class<? extends Throwable> e) throws Exception {
        Assert.assertThrows(e, () -> HDF5Utils.isHDF5Stream(invalidStream));
    }

    @Test
    public void testIsHDF5Stream() throws Exception {
        // HDF5 stream
        final InputStream hdf5SignatureStream = new ByteArrayInputStream(
                Bytes.toArray(Ints.asList(HDF5Constants.FORMAT_SIGNATURE)));
        // check that a stream with the HDF5 signature returns true
        Assert.assertTrue(HDF5Utils.isHDF5Stream(hdf5SignatureStream));
        // and because it is not consumed, it could be passed again
        Assert.assertTrue(HDF5Utils.isHDF5Stream(hdf5SignatureStream));

        // Text stream
        final InputStream textStream = new ByteArrayInputStream("ABCD".getBytes());
        // check that some text is not HDF5 stream
        Assert.assertFalse(HDF5Utils.isHDF5Stream(textStream));
        // check that still could be read
        Assert.assertEquals(textStream.read(), 'A');
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

    private Path createTempFileWithContent(final String name, final String content)
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