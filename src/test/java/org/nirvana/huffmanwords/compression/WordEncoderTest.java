//package org.nirvana.CanonicalHuffman.compression;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import javax.naming.SizeLimitExceededException;
//import java.io.BufferedInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.HashMap;
//
//public class WordEncoderTest {
//
//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @Test
//    public void generateFrequencyMap() throws SizeLimitExceededException, IOException {
//        byte[] byteStream = "This is me today.  won't this be the same,AWWWww! ??䀲2.".getBytes();
//        BufferedInputStream br = new BufferedInputStream(new ByteArrayInputStream(byteStream));
//        CanonicalEncoder en = new CanonicalEncoder();
//        HashMap<String,Integer> hr = (HashMap<String, Integer>) en.generateFrequencyMap(br);
//        for(String i : hr.keySet())
//            System.out.println(i+": "+hr.get(i));
//    }
//
//    @Test
//    public void initQueue() {
//    }
//
//    @Test
//    public void buildHuffmanTree() {
//    }
//}