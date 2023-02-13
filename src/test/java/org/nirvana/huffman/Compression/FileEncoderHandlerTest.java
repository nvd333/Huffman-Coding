package org.nirvana.huffman.Compression;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nirvana.huffman.HuffmanCodeTree;
import org.nirvana.utils.TreeNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileEncoderHandlerTest {

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ByteArrayInputStream streamIn;
    byte[] buf;
    TreeNode root = new TreeNode(null,null);
    @Mock
    HuffmanCodeTree codes = new HuffmanCodeTree(root);
    @InjectMocks
    FileEncoderHandler fileEncoderHandler = new FileEncoderHandler(1,byteOut);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void writeText_WhenASingleCharacterIsWritten() throws Exception {
        HashMap<Integer,String> codeset = new HashMap<>();
        codeset.put(65,"0");
        when(codes.getHuffmanCodeSet()).thenReturn(codeset);
        when(codes.getBinaryTreeAsBitString()).thenReturn("101000001");
        buf = new byte[]{(byte) 65,(byte) 65,(byte) 65};
        streamIn = new ByteArrayInputStream(buf);
        fileEncoderHandler = new FileEncoderHandler(3,byteOut);
        fileEncoderHandler.writeText(codes,streamIn);
        byte[] expected = new byte[]{0,(byte)-112,0,0,0,(byte) 58,(byte)8};
        assertTrue(Arrays.equals(expected,byteOut.toByteArray()));
    }
}