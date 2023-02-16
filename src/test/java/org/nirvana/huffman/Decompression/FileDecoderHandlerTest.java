package org.nirvana.huffman.Decompression;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.nirvana.utils.TreeNode;

import java.io.*;

import static org.junit.Assert.*;

public class FileDecoderHandlerTest {
    FileDecoderHandler fileDecoderHandler;
    ByteArrayInputStream byteInput;
    byte[] buffer;

    @Test
    public void readHeader_WhenStreamIsAvailable() throws IOException {
        buffer = new byte[]{0,0b1010000,0,0,0,(byte)1111};
        byteInput = new ByteArrayInputStream(buffer);
        fileDecoderHandler = new FileDecoderHandler(new BufferedInputStream(byteInput));
        assertEquals(5,fileDecoderHandler.readHeader());
    }
    @Test(expected = IllegalStateException.class)
    public void bitString2BinaryTree_WhenNoString() {
        buffer = new byte[]{0};
        byteInput = new ByteArrayInputStream(buffer);
        fileDecoderHandler = new FileDecoderHandler(new BufferedInputStream(byteInput));
        fileDecoderHandler.BitString2BinaryTree("");
    }
    @ParameterizedTest
    @CsvSource({"101000001,65","110000001,129"})
    public void bitString2BinaryTree_WithShortestBitString(String bitString,int expected){
        buffer = new byte[]{0,0b1010000,0,0,0,(byte)1111,23,2,32,13};
        byteInput = new ByteArrayInputStream(buffer);
        fileDecoderHandler = new FileDecoderHandler(new BufferedInputStream(byteInput));
        TreeNode actual = fileDecoderHandler.BitString2BinaryTree(bitString);
        assertEquals(expected,actual.getAsciiTag());
    }
    @ParameterizedTest
    @CsvSource({"0101000001110000001,65,129"})
    public void bitString2BinaryTree_WithABitString(String bitString,int left,int right){
        buffer = new byte[]{0,0b1010000,0,0,0,(byte)1111,23,2,32,13};
        byteInput = new ByteArrayInputStream(buffer);
        fileDecoderHandler = new FileDecoderHandler(new BufferedInputStream(byteInput));
        TreeNode root = new TreeNode(new TreeNode(left,-100),new TreeNode(right,-100));
        TreeNode actual = fileDecoderHandler.BitString2BinaryTree(bitString);
        assertEquals(root.getLeft().getAsciiTag(),actual.getLeft().getAsciiTag());
    }

    @Test
    public void writeText_WithStreamAvailable() throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(20);
        buffer = new byte[]{0,Byte.parseByte("0100000",2),0,0,0,Byte.parseByte("1000000",2)};
        byteInput = new ByteArrayInputStream(buffer);
        fileDecoderHandler = new FileDecoderHandler(new BufferedInputStream(byteInput));
        fileDecoderHandler.readHeader();
        TreeNode root = new TreeNode(65,-100);
        BufferedOutputStream buffOut = new BufferedOutputStream(byteOut);
        fileDecoderHandler.writeText(buffOut,root);
        buffOut.flush();
        assertEquals("AAAA",byteOut.toString());

    }
    @Test
    public void writeText_WithStreamAvailableWithMoreCharacter() throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(20);
        buffer = new byte[]{Byte.parseByte("0000010",2),0,0,0,0,Byte.parseByte("1100010",2),Byte.parseByte("01010111",2)};
        byteInput = new ByteArrayInputStream(buffer);
        fileDecoderHandler = new FileDecoderHandler(new BufferedInputStream(byteInput));
        fileDecoderHandler.readHeader();
        TreeNode root = new TreeNode(new TreeNode(65,-50),new TreeNode(100,-50));
        BufferedOutputStream buffOut = new BufferedOutputStream(byteOut);
        fileDecoderHandler.writeText(buffOut,root);
        buffOut.flush();
        assertEquals("AAdAAd",byteOut.toString());

    }


}