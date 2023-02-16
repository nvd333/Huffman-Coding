package org.nirvana.wordHuffman;

import org.junit.Assert;
import org.junit.Test;
import org.nirvana.utils.TreeNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class wordHuffmanCodeTreeTest {

    @Test
    public void huffTreeAsBitString() throws IOException, ClassNotFoundException {
        TreeNode root = new TreeNode(new TreeNode("This",1234),new TreeNode(new TreeNode("my",32234),new TreeNode("god",32842)));
        String[] input = wordHuffmanCodeTree.HuffTreeAsBitString(root).split(",");
        byte[] bits = new byte[input.length];
        for(int i =0 ;i<input.length;i++)
            bits[i] = (byte) Integer.parseInt(input[i]);


        System.out.println(input.toString()+"   ");



        ByteArrayInputStream bytein = new ByteArrayInputStream(bits);
        byte[] buf = bytein.readNBytes(59);
        Assert.assertEquals(root.getLeft().getWordLabel(),wordHuffmanCodeTree.deserialize(new ObjectInputStream(new ByteArrayInputStream(buf))).getLeft().getWordLabel());

    }
}