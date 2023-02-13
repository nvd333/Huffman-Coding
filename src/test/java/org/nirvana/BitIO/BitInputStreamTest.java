package org.nirvana.BitIO;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class BitInputStreamTest {

    BitInputStream bitInputStream;
    byte[] buffer;
    @Before
    public void setUp() throws Exception {
        buffer = new byte[]{(byte)0b00101010,(byte)0b11010110};
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = NullPointerException.class)
    public void read_WhenStreamIsNull(){
            bitInputStream = new BitInputStream(null);
    }
    @ParameterizedTest
    @CsvSource({"3,001","4,0010","2,00","5,00101"})
    public void read_WhenReadLessThanByte(int numbits,String expected) throws IOException {
        buffer = new byte[]{(byte)0b00101010,(byte)0b11010110};
        bitInputStream = new BitInputStream(new ByteArrayInputStream(buffer));
        Assert.assertEquals(expected,bitInputStream.read(numbits));
    }

    @ParameterizedTest
    @CsvSource({"9,001010101","14,00101010110101","12,001010101101"})
    public void read_WhenReadMoreThanByte(int numbits,String expected) throws IOException {
        buffer = new byte[]{(byte)0b00101010,(byte)0b11010110};
        bitInputStream = new BitInputStream(new ByteArrayInputStream(buffer));
        Assert.assertEquals(expected,bitInputStream.read(numbits));
    }

    @Test(expected = EOFException.class)
    public void read_MoreThanStream() throws IOException
    {
        buffer = new byte[]{(byte) 0b010101};
        bitInputStream = new BitInputStream(new ByteArrayInputStream(buffer));
        bitInputStream.read(16);
    }

    @ParameterizedTest
    @CsvSource({"0,8","3,5","6,2"})
    public void availableBits(int readBit,int expected) throws IOException {
        buffer = new byte[]{0b1010111};
        bitInputStream = new BitInputStream(new ByteArrayInputStream(buffer));
        bitInputStream.read(readBit);
        Assert.assertEquals(expected,bitInputStream.availableBits());
    }



}