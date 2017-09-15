package org.magicdgs.hdf5j.fileformat.level1;

/**
 * Free-space managers are used to describe space within a heap or the entire HDF5 file that is not
 * currently used for that heap or file. Note that the Format Signature is not included in
 * this layout, because it is represented by the constant
 * {@link org.magicdgs.hdf5j.HDF5Constants#FREE_SPACE_MANAGER_SIGNARUTE}.
 *
 * <p>The free-space manager header contains metadata information about the space being tracked,
 * along with the address of the list of free space sections which actually describes the free
 * space. The header records information about free-space sections being tracked, creation
 * parameters for handling free-space sections of a client, and section information used to locate
 * the collection of free-space sections.
 *
 * <p>The free-space section list stores a collection of free-space sections that is specific to
 * each client of the free-space manager. For example, the fractal heap is a client of the free
 * space manager and uses it to track unused space within the heap. There are 4 types of section
 * records for the fractal heap, each of which has its own format, listed below.
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
// TODO: extend the HDF5FileBlock
public interface FreeSpaceManager {

    /**
     * Gets the <b>Client ID</b>.
     *
     * <p>This is the client ID for identifying the user of this free-space manager:
     *
     * <table>
     * <tr><th>ID</th><th>Description</th></tr>
     * <tr><td>0</td><td>Fractal heap</td></tr>
     * <tr><td>1</td><td>File</td></tr>
     * <tr><td>2+</td><td>Reserved</td></tr>
     * </table>
     *
     * @return unsigned byte representing the client ID.
     */
    public int getClientID();

}
