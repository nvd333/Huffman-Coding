package org.nirvana.wordHuffman.compression;

import org.nirvana.BitIO.BitOutputStream;
import org.nirvana.huffman.Compression.encoderFileIO;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class WordCodeFileEncodeHandler implements encoderFileIO {
    int noOfCharacters;
    BitOutputStream bitOutStream;

    public WordCodeFileEncodeHandler(int noOfCharacters, OutputStream out){
        this.bitOutStream = new BitOutputStream(new BufferedOutputStream(out));
        this.noOfCharacters = noOfCharacters;
    }


    /**
     * Writes to the Stream
     *
     * @param binString   contains String Representation of Tree
     * @param huffCodeSet contains Huffman Code Set
     * @param in          InputStream
     * @throws Exception if error is encountered
     */
    @Override
    public void writeText(String binString, HashMap<?, String> huffCodeSet, InputStream in) throws Exception {

        String[] input = binString.split(",");
        byte[] bits = new byte[input.length];
        for(int i =0 ;i<input.length;i++)
            bits[i] = (byte) Integer.parseInt(input[i]);
        System.out.println("Header Length: "+((24+32+bits.length*8)/(8*1024))+" kb");
        bitOutStream.write(bits.length,32);
        bitOutStream.write(this.noOfCharacters,32);

        bitOutStream.write(bits);
        writePlainText((HashMap<Integer, String>) huffCodeSet,in);
        bitOutStream.close();


    }
    private void writePlainText(HashMap<Integer,String> huffCodeSet,InputStream in) throws IOException {
        int c;
        StringBuilder word = new StringBuilder();
        while((c = in.read()) != -1) {
            char asciiSymbol = (char) c;
            if(Character.isLetter(asciiSymbol))
                word.append(asciiSymbol);
            else {
                if (word.length() != 0) {
                    //
                    //word.append(asciiSymbol);
                    bitOutStream.write(huffCodeSet.get(word.toString()));
                    word.setLength(0);
                }
                //
                //else
                if(huffCodeSet.get(""+(int)asciiSymbol) !=null)
                    bitOutStream.write(huffCodeSet.get(""+(int)asciiSymbol));
                else
                    System.out.println((int)asciiSymbol+":"+huffCodeSet.get(""+(int)asciiSymbol));
            }
        }
        if(word.length()!=0)
            bitOutStream.write(huffCodeSet.get(word.toString()));
    }


}
