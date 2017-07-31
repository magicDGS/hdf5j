package org.magicdgs.hdf5j.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;

/**
 * Wrapper around {@link SeekableByteChannel} implementing {@link DataOutput} for write primitive
 * data types with a custom {@link ByteOrder}.
 * <p>
 * One of the usages of this {@link DataOutput} is to write any data type in little-endian
 * ({@link ByteOrder#LITTLE_ENDIAN}).
 *
 * @author Daniel Gomez-Sanchez (magicDGS)
 */
// TODO: at some point this could implement ObjectOutput to be able to read arbitrary Java objects
public class SeekableDataOutput implements SeekableByteChannel, DataOutput {

    private final static int BUFFER_SIZE = 64 * 1024;

    private final SeekableByteChannel channel;
    private ByteBuffer writerBuffer;


    public SeekableDataOutput(final SeekableByteChannel channel, final ByteOrder byteOrder) {
        this(channel, byteOrder, BUFFER_SIZE);
    }

    /**
     * Constructor for checking buffer size issues.
     *
     * @param channel    open chanel.
     * @param byteOrder  byte order for the
     * @param bufferSize the writerBuffer size (should use {@link #BUFFER_SIZE} to be consistent).
     */
    @VisibleForTesting
    SeekableDataOutput(final SeekableByteChannel channel, final ByteOrder byteOrder,
            final int bufferSize) {
        Preconditions.checkArgument(channel.isOpen(), "SeekableByteChannel is not open");
        this.channel = channel;
        this.writerBuffer = ByteBuffer.allocate(bufferSize).order(byteOrder);
        resetBuffer();
    }

    /**
     * Returns the byte order used by to read/write the data.
     */
    public ByteOrder byteOrder() {
        return writerBuffer.order();
    }

    /**
     * @throws UnsupportedOperationException because reading is not supported for {@link
     *                                       SeekableDataOutput}. Use {@link SeekableDataInput}
     *                                       instead.
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        throw new UnsupportedOperationException("SeekableDataOutput does not support reading");
    }

    @Override
    public SeekableDataOutput truncate(final long size) throws IOException {
        channel.truncate(size);
        return this;
    }

    @Override
    public int write(final ByteBuffer src) throws IOException {
        // check conditions
        Preconditions.checkArgument(src != null, "null ByteBuffer");
        Preconditions.checkArgument(byteOrder().equals(src.order()),
                "ByteBuffer should be in %s byte-order, but it is in %s",
                byteOrder(), src.order());

        return channel.write(src);
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public SeekableDataOutput position(long newPosition) throws IOException {
        channel.position();
        return this;
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    private void resetBuffer() {
        writerBuffer.clear();
        writerBuffer.flip();
    }

    private void writeAndReset() throws IOException {
        writerBuffer.flip();
        channel.write(writerBuffer);
        resetBuffer();
    }

    @Override
    public void write(int b) throws IOException {
        writerBuffer.put((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        channel.write(ByteBuffer.wrap(b, off, len));
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        final byte b = v ? (byte) 1 : (byte) 0;
        writerBuffer.put(b);
        writeAndReset();
    }

    @Override
    public void writeByte(int v) throws IOException {
        write(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        writerBuffer.putShort((short) v);
        writeAndReset();
    }

    @Override
    public void writeChar(int v) throws IOException {
        writerBuffer.putChar((char) v);
        writeAndReset();
    }

    @Override
    public void writeInt(int v) throws IOException {
        writerBuffer.putInt(v);
        writeAndReset();
    }

    @Override
    public void writeLong(long v) throws IOException {
        writerBuffer.putLong(v);
        writeAndReset();
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writerBuffer.putFloat(v);
        writeAndReset();
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writerBuffer.putDouble(v);
        writeAndReset();
    }

    @Override
    public void writeBytes(final String s) throws IOException {
        // throw null pointer exception if null
        Preconditions.checkNotNull(s, "null string");
        // only write if it is not empty (contract)
        final int len = s.length();
        // add to the buffer each character as a byte
        for (int i = 0; i < len; i++) {
            if (!writerBuffer.hasRemaining()) {
                // if it is already full, write it down
                writeAndReset();
            }
            // append the char casted to byte to the buffer
            writerBuffer.put((byte) s.charAt(i));
        }
    }

    @Override
    public void writeChars(final String s) throws IOException {
        // throw a null pointer exception if null
        Preconditions.checkNotNull(s, "null string");
        // only write if it is not empty (contract)
        final int len = s.length();
        // add to the buffer each character as a byte
        for (int i = 0; i < len; i++) {
            if (!writerBuffer.hasRemaining()) {
                // if it is already full, write it down
                writeAndReset();
            }
            // append the char to the buffer
            writerBuffer.putChar(s.charAt(i));
        }
    }

    /**
     * @implNote it uses the code in {@link java.io.DataOutputStream#writeUTF(String, DataOutput)},
     * which is static but package-private, so it is copied directly from there.
     */
    @Override
    public void writeUTF(final String s) throws IOException {
        int strlen = s.length();
        int utflen = 0;
        int c, count = 0;

        /* use charAt instead of copying String to char array */
        for (int i = 0; i < strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        if (utflen > 65535) {
            throw new UTFDataFormatException(
                    "encoded string too long: " + utflen + " bytes");
        }

        byte[] bytearr = new byte[utflen + 2];

        bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);

        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = s.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            bytearr[count++] = (byte) c;
        }

        for (; i < strlen; i++) {
            c = s.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;

            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        write(bytearr, 0, utflen + 2);
    }


    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
        resetBuffer();
    }
}
