package org.nirvana.huffmanstatic;

import org.nirvana.utils.CodeTree;
import org.nirvana.utils.TreeNode;

import java.util.HashMap;
import java.util.InputMismatchException;

public class HuffmanCodeTree implements CodeTree {

    HashMap<Integer,String> HuffmanCodeSet;
    String BinaryTreeAsBitString;
    public String getBinaryTreeAsBitString() { return BinaryTreeAsBitString; }
    public HashMap<Integer, String> getHuffmanCodeSet() {
        return HuffmanCodeSet;
    }

    public HuffmanCodeTree(TreeNode root)
    {
        HuffmanCodeSet = new HashMap<>();
        if(root == null) throw new NullPointerException("ERROR: Tree is Null");
        if(root.isLeaf())
            HuffmanCodeSet.put(root.getAsciiTag(), "0");
        else
            generateCode(root, "");
        BinaryTreeAsBitString = HuffTreeAsBitString(root);
    }

    private void generateCode(TreeNode node, String nodeCodeString)
    {
        if (node != null) {
            if(node.isLeaf())
                HuffmanCodeSet.put( node.getAsciiTag(), nodeCodeString);
            generateCode(node.getLeft(), nodeCodeString+"0");
            generateCode(node.getRight(), nodeCodeString + "1");
        }
    }

    @Override
    public String HuffTreeAsBitString(TreeNode root) {
        if (root == null) return "";
        return this.toBitString(root);
    }

    /**
     * Appends 1 with 8 bit representation of ASCII character or returns 0
     * @param node TreeNode whose representation to be generated
     * @return 9 bit String or 1 bit String
     */
    static String getAsciiLabel(TreeNode node) {
        if (node == null) throw new IllegalStateException("ERROR: Encountered a null where it shouldn't have");
        if (node.isLeaf()) return "1" + Integer.toBinaryString((node.getAsciiTag() & 0xFF) + 0x100).substring(1);
        return "0";
    }

    /**
     * Represents a Tree as bit String
     *
     * @param root TreeNode
     * @return  bit-representation of the Tree
     * @throws IllegalStateException if Tree is not of type binary Tree
     */

    private String toBitString(TreeNode root) throws IllegalStateException{
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(getAsciiLabel(root));

        if (root.getLeft() != null) sb.append(toBitString(root.getLeft()));

        if (root.getRight() != null) {
            if (root.getLeft() == null)
                throw new IllegalStateException("ERROR: Error in computation of Encoded String of Huffman Tree");
            sb.append(toBitString(root.getRight()));
        }
        return sb.toString();
    }

    /**
     * Returns Huffman Code for a character
     *
     * @param character whose huffman code is desired
     * @return  String that is huffman code of the input character
     */
    @Override
    public String getHuffCode(int character) {
        if (HuffmanCodeSet.size() == 0)
            throw new IndexOutOfBoundsException("ERROR: HuffmanCodes Set is Empty");
        if (character > -1 && character < 256)
            return HuffmanCodeSet.get((char)character);
        throw new InputMismatchException("ERROR: Input Character not in range");

    }

    public static String PrintTree(TreeNode root) { return CodeTree.traversePreOrder(root);}

    public double avgCodeLength(int[] freq)
    {
        double term;
        double numerator = 0;
        double denominator = 0;
        for(int i : HuffmanCodeSet.keySet()) {
            if (!(i >= 0 && i <= 255))
                continue;
            term = HuffmanCodeSet.get(i).length() * freq[i];
            numerator += term;
            denominator += freq[i];
        }
        return numerator/denominator;
    }
}
