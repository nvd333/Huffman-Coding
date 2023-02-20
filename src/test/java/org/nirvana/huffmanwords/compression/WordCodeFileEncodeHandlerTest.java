package org.nirvana.huffmanwords.compression;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class WordCodeFileEncodeHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = NullPointerException.class)
    public void writeText_whenNull() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        WordCodeFileEncodeHandler wordCodeFileEncodeHandler = new WordCodeFileEncodeHandler(0,byteArrayOutputStream);
        wordCodeFileEncodeHandler.writeText(null,null,null);
    }

    @Test
    public void writeText_whenArgumentsArePresent() throws Exception {
        String bitStr = "2,65,12";
        HashMap<String, String> codeSet = new HashMap<>();
        codeSet.put("A","1");
        byte[] expected = new byte[]{0,0,0,3,0,0,0,1(byte) 2,(byte)65,(byte)12}
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("A".getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        WordCodeFileEncodeHandler wordCodeFileEncodeHandler = new WordCodeFileEncodeHandler(1,byteArrayOutputStream);
        wordCodeFileEncodeHandler.writeText(bitStr,codeSet,byteArrayInputStream);
        Assert.assertEquals("0",")");
    }

    @Test
    public void writeWordIfPresentElseIndividualCharacter() {
    }
}