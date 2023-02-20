package org.nirvana.bitIO;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements BitOutputStreamable,AutoCloseable{
    protected BufferedOutputStream out;
    protected int buffer;
    protected int bufferBitCount;

    public BitOutputStream(OutputStream out){
        if(out == null) throw new NullPointerException("ERROR: OutputStream is Null");
        this.out = new BufferedOutputStream(out);
        this.bufferBitCount = 0;
    }

    /**
     * Write some bits
     *
     * @param b which can be either 1 or 0
     * @throws IllegalArgumentException when parameter is not O or 1
     * @throws IOException if I/O error occurs
     */
    @Override
    public void write(int b) throws IOException {
        if (b != 0 && b != 1)
            throw new IllegalArgumentException("Argument must be 0 or 1");
        buffer = (buffer << 1) | b;
        bufferBitCount++;
        if (bufferBitCount== 8) {
            out.write(buffer);
            buffer = 0;
            bufferBitCount = 0;
        }
    }

    /**
     * Facilitates writing of any number of bits
     *
     * @param data which is not be written gets converted into binary Representation
     * @param numbits ie number of bits to be written
     * @return Binary Representation of the input parameter
     * @throws IOException if I/O error occurs
     * @throws IllegalStateException if numbits are less than binary representation of data
     */
    public String write(int data,int numbits) throws IOException {
        String bitStr = String.format("%" + numbits + "s", Integer.toBinaryString(data)).replace(' ', '0');
        if (bitStr.length() != numbits)
            throw new IllegalStateException("ERROR: bits can't be written: "+bitStr+" greater "+numbits);
        for (int i = 0; i < bitStr.length(); i++)
            this.write(Character.getNumericValue(bitStr.charAt(i)));
        return bitStr;
    }

    /**
     * Takes in input as a Binary String and writes
     * @param bitStr binary String
     * @throws IOException if I/O error occurs
     */
    public void write(String bitStr) throws IOException
    {
        for(int j = 0; j < bitStr.length(); j ++)
            this.write(Character.getNumericValue(bitStr.charAt(j)));
    }

    public void write(byte[] bytes) throws IOException {
        out.write(bytes);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        while(bufferBitCount !=0)
            this.write(0);
        out.close();
    }

}
