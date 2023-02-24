package org.nirvana.huffmanwords.decompression;

import org.apache.commons.lang3.math.NumberUtils;
import org.nirvana.bitIO.BitInputStream;
import org.nirvana.huffmanwords.WordHuffmanCodeTree;
import org.nirvana.utils.TreeNode;

import java.io.*;
import java.util.HashMap;

public class WordCodeFileDecoderHandler {
    BitInputStream bitInStream;
    int noOfCharacters;
    public WordCodeFileDecoderHandler(InputStream in) {
        this.bitInStream = new BitInputStream(in);
        this.noOfCharacters = 0;
    }

    public HashMap<String, Integer> readHeader() throws IOException, ClassNotFoundException {
        //int bitStrLen = Integer.parseUnsignedInt(bitInStream.read(32),2);
        HashMap<String,Integer> frequencyMap = (HashMap<String, Integer>) bitInStream.readObject();
        this.noOfCharacters = Integer.parseUnsignedInt(bitInStream.read(32),2);
        return frequencyMap;
    }

    public TreeNode getBitTree2BinaryTree(int length) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[length];
        for(int i = 0; i < length; i ++)
            bytes[i] = (byte) Integer.parseInt(bitInStream.read(8),2);
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(bytes,0,length);
        return WordHuffmanCodeTree.deserialize(new ObjectInputStream(byteArrayStream));
    }

    public HashMap<String, Integer> getBitTree2FrequencyMap(int length) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[length];
        for(int i = 0; i < length; i ++)
            bytes[i] = (byte) Integer.parseInt(bitInStream.read(8),2);
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(bytes,0,length);
        ObjectInputStream obj = new ObjectInputStream(byteArrayStream);
        return (HashMap<String, Integer>) obj.readObject();
    }


    public void writeText(OutputStream out, TreeNode root) throws Exception {
        int c;
        if(root==null && noOfCharacters!=0)
            throw new IllegalStateException("ERROR: Binary Tree is null");

        TreeNode current = root;
        if(current.isLeaf())
            while(this.noOfCharacters != 0 ) {
                String word = current.getWordLabel();
                if(NumberUtils.isParsable(word)){
                    out.write((char)Integer.parseInt(word));
                }else{
                    for(char l: word.toCharArray())
                        out.write(l);
                }
                noOfCharacters --;
            }
        while(this.noOfCharacters != 0) {
            c = bitInStream.read();
            if (c == 0)
                current = current.getLeft();
            else if (c == 1)
                current = current.getRight();
            if (current.isLeaf()) {
                String word = current.getWordLabel();
                try {
                    int  num = Integer.parseInt(word);
                    out.write((char)num);
                } catch (NumberFormatException e) {
                    for(char l: word.toCharArray())
                        out.write(l);
                }
                current = root;
                this.noOfCharacters --;
            }
        }
    }

}
