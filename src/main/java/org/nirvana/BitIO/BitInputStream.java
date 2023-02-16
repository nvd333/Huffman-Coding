package org.nirvana.BitIO;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream implements BitInputStreamable,AutoCloseable{
    protected BufferedInputStream in;
    protected int buffer;
    protected int bufferBitCount;

    public BitInputStream(InputStream in)
    {
        if(in == null) throw new NullPointerException("ERROR: InputStream is Null");
        this.in = new BufferedInputStream(in);
        this.bufferBitCount = 0;
    }

    /**
     * Returns Available number of bit in the stream
     * @return  available bits yet to be read
     * @throws IOException  when Input-stream is not available or encounters an error
     */
    public int availableBits() throws IOException {
        return  (in.available()*BITS_PER_BYTE) + bufferBitCount;
    }

    /**
     * Closes the stream
     * @throws IOException if I/O error occurs
     */

    public void close() throws IOException{
        bufferBitCount = -1;
        in.close();
    }

    /**
     * Returns a single bit by maintaining a buffer
     *
     * @return an integer whose lower bits are read from stream which is either 0 or 1
     * @throws EOFException if no data available from the InputStream
     * @throws IOException  if I/O error occurs
     */

    @Override
    public int read() throws IOException {
        if (buffer == -1)
            return -1;
        if (bufferBitCount == 0) {
            buffer = in.read();
            if (buffer == -1)
                return -1;
            bufferBitCount = 8;
        }
        if (bufferBitCount <= 0)
            throw new AssertionError();
        bufferBitCount--;
        return (buffer >>> bufferBitCount) & 1;
    }

    /**
     * Facilitates reading of multiple bits
     * @param numbits ie Number of bits to be read
     * @return Binary String of length specified number of bits
     * @throws IOException  if I/O error occurs
     * @throws EOFException if End of File is encountered
     */
    public String read(int numbits) throws IOException {
        StringBuilder bitString = new StringBuilder();
        while(numbits!=0) {
            int c = this.read();
            numbits -= 1;

            if(c == -1)
                throw new EOFException();
            bitString.append(c);
        }
        return bitString.toString();
    }
}
