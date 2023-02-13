package org.nirvana.BitIO;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;



public class BitOutputStreamTest {
    BitOutputStream bitOutputStream;
    ByteArrayOutputStream byteStream;

    @Before
    public void setUp() throws Exception {
        byteStream = new ByteArrayOutputStream();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = NullPointerException.class)
    public void write_WhenStreamIsNull(){
        bitOutputStream = new BitOutputStream(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void write_Not0Or1() throws IOException{
        bitOutputStream = new BitOutputStream(byteStream);
        bitOutputStream.write(23);
    }

    @ParameterizedTest
    @CsvSource({"010000110110,01000011","00101110101011,00101110"})
    public void writeString_MoreThanAByte(String bitStr,String expected) throws IOException{
        byteStream = new ByteArrayOutputStream();
        bitOutputStream = new BitOutputStream(byteStream);
        bitOutputStream.write(bitStr);
        Assert.assertEquals(""+(char)Integer.parseInt(expected,2),byteStream.toString());
    }

    @Test
    public void writeString_LessThanAByte() throws IOException{
        bitOutputStream = new BitOutputStream(byteStream);
        bitOutputStream.write("1111");
        Assert.assertEquals("",byteStream.toString());
    }

    @ParameterizedTest
    @CsvSource({"65,12,000001000001","98,16,0000000001100010"})
    public void WriteNumber_WhenNumBitsMoreThanData(int data,int numBits,String expected) throws IOException {
        byteStream = new ByteArrayOutputStream();
        bitOutputStream = new BitOutputStream(byteStream);
        Assert.assertEquals(expected,bitOutputStream.write(data,numBits));
    }

    @Test(expected = IllegalStateException.class)
    public void WriteNumber_WithNumBitsLessThanData() throws IOException{
        byteStream = new ByteArrayOutputStream();
        bitOutputStream = new BitOutputStream(byteStream);
        bitOutputStream.write(65,2);
    }
}