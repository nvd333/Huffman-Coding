package org.nirvana.huffman.Compression;

import java.io.InputStream;
import java.util.HashMap;


public interface encoderFileIO {
    /**
     * Writes to the Stream
     *
     * @param binString contains String Representation of Tree
     * @param huffCodeSet contains Huffman Code Set
     * @param in    InputStream
     * @throws Exception if error is encountered
     */
    void writeText(String binString, HashMap<?,String> huffCodeSet, InputStream in) throws Exception;
}
