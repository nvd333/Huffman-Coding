package org.nirvana.huffmanstatic.Compression;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.nirvana.huffmanstatic.HuffmanCodeTree;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileEncoderHandlerTest {

    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    ByteArrayInputStream streamIn;
    byte[] buf;
    @Mock
    HuffmanCodeTree codes;
    @InjectMocks
    FileEncoderHandler fileEncoderHandler = new FileEncoderHandler(1,new BufferedOutputStream(byteOut));


    @Test
    public void writeText_WhenASingleCharacterIsWritten() throws Exception {
        HashMap<Integer,String> codeset = new HashMap<>();
        codeset.put(65,"0");
        when(codes.getHuffmanCodeSet()).thenReturn(codeset);
        when(codes.getBinaryTreeAsBitString()).thenReturn("101000001");
        buf = new byte[]{(byte) 65,(byte) 65,(byte) 65};
        streamIn = new ByteArrayInputStream(buf);
        fileEncoderHandler = new FileEncoderHandler(3,new BufferedOutputStream(byteOut));
        fileEncoderHandler.writeText(codes.getBinaryTreeAsBitString(),codes.getHuffmanCodeSet(),new BufferedInputStream(streamIn));
        byte[] expected = new byte[]{0,(byte)-112,0,0,0,(byte) 58,(byte)8};
        assertArrayEquals(expected, byteOut.toByteArray());
    }
}