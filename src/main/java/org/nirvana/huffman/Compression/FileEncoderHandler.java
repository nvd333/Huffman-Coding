package org.nirvana.huffman.Compression;

import org.nirvana.BitIO.BitOutputStream;
import org.nirvana.huffman.HuffmanCodeTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public class FileEncoderHandler implements encoderFileIO{
    int noOfCharacters;
    BitOutputStream bitOutStream;
    public FileEncoderHandler(int noOfCharacters,OutputStream out)
    {
        this.noOfCharacters = noOfCharacters;
        this.bitOutStream = new BitOutputStream(out);
    }

    /**
     * Writes to the stream
     *
     * @param codes which contains HuffmanCodes for each character
     * @param in    InputStream
     * @throws Exception if error occurs
     */
    @Override
    public void writeText(HuffmanCodeTree codes, InputStream in) throws Exception {
        String TreeBitString = codes.getBinaryTreeAsBitString();
        writeHeaders(TreeBitString);
        writeHuffmanTree(TreeBitString);
        writePlainText(codes.getHuffmanCodeSet(),in);
        bitOutStream.close();
    }

    /**
     * Writes header to stream
     *
     * @param TreeBitString Binary Representation of the Binary Tree
     * @throws IOException if I/O error occurs
     */
    private void writeHeaders(String TreeBitString) throws IOException {
        int bitStrLen = TreeBitString.length();
        bitOutStream.write(bitStrLen,12);
        bitOutStream.write(this.noOfCharacters,32);
    }

    /**
     * Writes a Binary Representation of Binary Tree to stream
     *
     * @param TreeBitString bit-Representation of Binary Tree
     * @throws IOException if I/O error occurs
     */
    private void writeHuffmanTree(String TreeBitString) throws IOException{
        bitOutStream.write(TreeBitString);
    }

    /**
     * Writes encoded text to the stream
     *
     * @param huffCodeSet Encoding codes of characters
     * @param in InputStream
     * @throws IOException if I/O error occurs
     */

    private void writePlainText(HashMap<Integer,String> huffCodeSet,InputStream in) throws IOException {
        int c;
        while((c = in.read())!=-1) {
            String s = huffCodeSet.get(c);
            bitOutStream.write(s);
        }
    }
}

