package org.magicdgs.hdf5j;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class to get test resources and test if they exist.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
public final class HDF5jTestResources extends HDF5jTest {

    /** Absolute Path to test resources. */
    private static final Path TEST_RESOURCES_DIR =
            Paths.get("src", "test", "resources").toAbsolutePath();

    /** Path containing the examples by API. */
    private static final Path EXAMPLES_BY_API_FOLDER =
            TEST_RESOURCES_DIR.resolve("HDF5").resolve("exbyapi");

    /** Test that the resources directories exist. */
    @Test
    public void testFileResources() {
        assertDirectoryExists(TEST_RESOURCES_DIR);
        assertDirectoryExists(EXAMPLES_BY_API_FOLDER);
        // the files in exbyapi should contain H5 files
        Assert.assertFalse(getExamplesByApi().isEmpty(),
                "no H5 files in " + EXAMPLES_BY_API_FOLDER.toAbsolutePath().toString());
    }

    private static void assertDirectoryExists(final Path path) {
        Assert.assertTrue(Files.exists(path), path + " directory does not exist");
        Assert.assertTrue(Files.isDirectory(path), path + " is not a directory");
    }

    /** Gets all the examples by API test files as a List. */
    public static List<Path> getExamplesByApi() {
        try {
            return Files.list(EXAMPLES_BY_API_FOLDER)
                    .filter(f -> f.toString().endsWith(".h5"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to retrieve files from " + EXAMPLES_BY_API_FOLDER);
        }
    }

}
