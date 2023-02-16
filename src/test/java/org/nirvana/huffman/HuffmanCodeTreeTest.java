package org.nirvana.huffman;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.nirvana.utils.TreeNode;

import java.util.InputMismatchException;

public class HuffmanCodeTreeTest {


    @Test(expected = NullPointerException.class)
    public void huffTreeAsBitString_WhenGivenANullTree() {
        HuffmanCodeTree huffmanCodeTree = new HuffmanCodeTree(null);
        huffmanCodeTree.HuffTreeAsBitString(null);
    }

    @Test
    public void huffTreeAsBitString_WhenGivenSingleNodeWhichIsNotLeaf() {
        TreeNode node = new TreeNode(null, null);
        HuffmanCodeTree huffmanCodeTree = new HuffmanCodeTree(node);
        Assert.assertEquals("100111110", huffmanCodeTree.HuffTreeAsBitString(node));
    }

    @ParameterizedTest
    @CsvSource({"65,34,101000001", "112,431,101110000"})
    public void huffTreeAsBitString_WhenGivenSingleNodeWhichIsLeafNode(int label, int frequency, String expected) {
        TreeNode leaf = new TreeNode(label, frequency);
        HuffmanCodeTree huffmanCodeTree = new HuffmanCodeTree(leaf);
        Assert.assertEquals(expected, huffmanCodeTree.HuffTreeAsBitString(leaf));
    }

    @Test
    public void huffTreeAsBitString_WhenGivenATree() {
        TreeNode node = new TreeNode(new TreeNode(54, 23), new TreeNode(32, 324));
        HuffmanCodeTree huffmanCodeTree = new HuffmanCodeTree(node);
        Assert.assertEquals("0100110110100100000", huffmanCodeTree.HuffTreeAsBitString(node));
    }

    @Test(expected = InputMismatchException.class)
    public void getHuffCode_ToThrowException() {
        TreeNode node = new TreeNode(null, null);
        HuffmanCodeTree huffmanCodeTree = new HuffmanCodeTree(node);
        huffmanCodeTree.getHuffCode(999);
    }

    @Test
    public void PrintTree_WhenGivenATree(){
        TreeNode root = new TreeNode(new TreeNode(67,12),new TreeNode(121,19));
        Assert.assertEquals("*(31)\n├──C(12)\n└──y(19)",HuffmanCodeTree.PrintTree(root));
    }

}