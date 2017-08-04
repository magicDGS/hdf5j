package org.magicdgs.hdf5j.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

 /**
 * Wrapper around {@link SeekableByteChannel} implementing {@link DataInput} for retrieve primitive
 * data types with a custom {@link ByteOrder}.
 * <p>
 * One of the usages of this {@link DataOutput} is to get data encoded in in little-endian
  * ({@link ByteOrder#LITTLE_ENDIAN}).
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
// TODO: at some point this could implement ObjectInput to be able to read arbitrary Java objects
public class SeekableDataInput implements SeekableByteChannel, DataInput {

    private final static int BUFFER_SIZE = 64 * 1024;

    private final SeekableByteChannel channel;
    private ByteBuffer readerBuffer;


    public SeekableDataInput(final SeekableByteChannel channel, final ByteOrder byteOrder) {
        this(channel, byteOrder, BUFFER_SIZE);
    }

    /**
     * Opens a path in read mode with the provided byte-order.
     *
     * @param path  input for data
     * @param order byte-order for the data.
     *
     * @return data input for reading.
     *
     * @throws IOException if an IO error occurs.
     */
    public static SeekableDataInput openSeekableDataInput(final Path path, final ByteOrder order)
            throws IOException {
        return new SeekableDataInput(Files.newByteChannel(path, StandardOpenOption.READ), order);
    }

    /**
     * Constructor for checking buffer size issues.
     *
     * @param channel    open chanel.
     * @param byteOrder  byte order for the
     * @param bufferSize the readerBuffer size (should use {@link #BUFFER_SIZE} to be consistent).
     */
    @VisibleForTesting
    SeekableDataInput(final SeekableByteChannel channel, final ByteOrder byteOrder,
            final int bufferSize) {
        Preconditions.checkArgument(channel.isOpen(), "SeekableByteChannel is not open");
        this.channel = channel;
        this.readerBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder);
        this.readerBuffer.clear();
        this.readerBuffer.flip();
    }

    /**
     * Returns the byte order used by to read/write the data.
     */
    public ByteOrder byteOrder() {
        return readerBuffer.order();
    }

    /** Fill the readerBuffer with enough data to read the next object with {@code size} bytes. */
    private void fillBuffer(final int size) throws IOException {
        Preconditions.checkState(size <= readerBuffer.capacity(),
                "Buffer cannot hold Objects with %s bytes (maximum %s bytes)",
                size, readerBuffer.capacity());
        // if the readerBuffer does not have enough remaining bytes
        if (readerBuffer.remaining() < size) {
            // put the bytes at the beginning (previous ones were already read)
            readerBuffer.compact();
            if (channel.read(readerBuffer) <= 0) {
                // end of the stream
                throw new EOFException("Unexpected end of data");
            }
            // come back to the beginning of the readerBuffer
            readerBuffer.flip();
        }
    }

    /**
     * @throws UnsupportedOperationException because writing is not supported for {@link
     *                                       SeekableDataInput}. Use {@link SeekableDataOutput}
     *                                       instead.
     */
    @Override
    public int write(final ByteBuffer src) throws IOException {
        throw new UnsupportedOperationException("SeekableDataInput does not support writing");
    }

    @Override
    public SeekableDataInput truncate(long size) throws IOException {
        channel.truncate(size);
        return this;
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
        // check conditions
        Preconditions.checkArgument(dst != null, "null ByteBuffer");
        Preconditions.checkArgument(byteOrder().equals(dst.order()),
                "ByteBuffer should be in %s byte-order, but it is in %s",
                byteOrder(), dst.order());

        return channel.read(dst);
    }

    @Override
    public SeekableDataInput position(long newPosition) throws IOException {
        channel.position(newPosition);
        return this;
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        // easier if the readerBuffer size is the same
        if (len <= BUFFER_SIZE) {
            fillBuffer(len);
            readerBuffer.get(b);
        } else {
            // starting from the first position
            int offset = 0;
            // start filling by readerBuffer size
            for (int i = BUFFER_SIZE; i < len; i += BUFFER_SIZE) {
                // read in all the readerBuffer
                fillBuffer(i);
                readerBuffer.get(b, offset, i);
                // now we start in the last index
                offset = i;
            }

        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        final long previousPosition = channel.position();
        channel.position(previousPosition + n);
        return (int) (channel.position() - previousPosition);
    }

    @Override
    public boolean readBoolean() throws IOException {
        fillBuffer(1);
        return readerBuffer.get() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        fillBuffer(1);
        return readerBuffer.get();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return Byte.toUnsignedInt(readByte());
    }

    @Override
    public short readShort() throws IOException {
        fillBuffer(Short.BYTES);
        return readerBuffer.getShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return Short.toUnsignedInt(readShort());
    }

    @Override
    public char readChar() throws IOException {
        fillBuffer(Character.BYTES);
        return readerBuffer.getChar();
    }

    @Override
    public int readInt() throws IOException {
        fillBuffer(Integer.BYTES);
        return readerBuffer.getInt();
    }

    @Override
    public long readLong() throws IOException {
        fillBuffer(Long.BYTES);
        return readerBuffer.getLong();
    }

    @Override
    public float readFloat() throws IOException {
        fillBuffer(Float.BYTES);
        return readerBuffer.getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        fillBuffer(Double.BYTES);
        return readerBuffer.getDouble();
    }

    @Override
    public String readLine() throws IOException {
        // TODO: should return null for end-of-file
        // TODO: currently it will throw!!
        // following the contract of readLine, this should read byte by byte and convert to char
        final StringBuilder str = new StringBuilder();
        char nextChar = (char) readByte();
        while (nextChar != '\n') {
            if (nextChar != '\r') {
                str.append(nextChar);
            }
        }
        return str.toString();
    }

    @Override
    public String readUTF() throws IOException {
        // using the implementation of DataInputStream
        return DataInputStream.readUTF(this);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
        readerBuffer.clear();
    }
}
