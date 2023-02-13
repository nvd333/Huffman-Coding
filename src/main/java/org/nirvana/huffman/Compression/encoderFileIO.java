package org.nirvana.huffman.Compression;

import org.nirvana.huffman.HuffmanCodeTree;

import java.io.InputStream;


public interface encoderFileIO {
    /**
     * Writes to the Stream
     *
     * @param huffmanCodeTree which contains HuffmanCodes for each character
     * @param in    InputStream
     * @throws Exception if error is encountered
     */
    void writeText(HuffmanCodeTree huffmanCodeTree, InputStream in) throws Exception;
}
