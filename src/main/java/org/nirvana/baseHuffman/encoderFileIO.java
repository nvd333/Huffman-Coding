package org.nirvana.baseHuffman;

import java.io.InputStream;
import java.util.HashMap;


public interface encoderFileIO<T> {
    /**
     * Writes to the Stream
     *
     * @param binString contains String Representation of Tree
     * @param huffCodeSet contains Huffman Code Set
     * @param in    InputStream
     * @throws Exception if error is encountered
     */
    void writeText(T binString, HashMap<?,String> huffCodeSet, InputStream in) throws Exception;
}
