package org.nirvana.huffman.Decompression;

import org.nirvana.BitIO.BitInputStream;
import org.nirvana.utils.TreeNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.MissingResourceException;
import java.util.Stack;

public class FileDecoderHandler implements decoderFileIO{
    BitInputStream bitInStream;
    int noOfCharacters;
    public FileDecoderHandler(InputStream in) {
        this.bitInStream = new BitInputStream(in);
        this.noOfCharacters = 0;
    }

    public int readHeader() throws IOException {
        int bitStrLen = Integer.parseUnsignedInt(bitInStream.read(12),2);
        this.noOfCharacters = Integer.parseUnsignedInt(bitInStream.read(32),2);
        return bitStrLen;
    }

    public String getBitTree2String(int length) throws IOException {
        return bitInStream.read(length);
    }

    public TreeNode BitString2BinaryTree(String BitString) {
        Stack<TreeNode> stack = new Stack<>();
        if(BitString == null || BitString.length()< 9) throw new IllegalStateException("ERROR: Header seems Empty");
        TreeNode root = new TreeNode(null, null);
        stack.push(root);
        int j = 1;
        if(BitString.length()==9)
            return new TreeNode(Integer.parseUnsignedInt(BitString.substring(1,9),2),-50);
        int identityBit;

        while (j < BitString.length())
        {
            identityBit = Character.getNumericValue(BitString.charAt(j++));
            TreeNode node = null;
            if (identityBit == 0)
                node = new TreeNode(null, null);
            else if(identityBit == 1){
                int character = Integer.parseInt(BitString.substring(j, j+=8), 2);
                node = new TreeNode(character, -100);
            }

            if (node == null)
                throw new IllegalStateException("ERROR: Null Encountered in BT Reconstruction");

            if (stack.peek().getLeft() == null) {
                stack.peek().setLeftBranch(node);
                if (identityBit == 0) stack.push(node);
            } else if (stack.peek().getRight() == null) {
                stack.peek().setRightBranch(node);
                if (identityBit == 0) stack.push(node);
            } else {
                while (stack.peek().getLeft() != null && stack.peek().getRight() != null)
                    stack.pop();
                stack.peek().setRightBranch(node);
                if (identityBit == 0) stack.push(node);
            }
        }
        return root;
    }

    @Override
    public void writeText(OutputStream out,TreeNode root) throws Exception {
        int c;
        if(root==null && noOfCharacters!=0)
            throw new IllegalStateException("ERROR: Binary Tree is null");

        TreeNode current = root;
        if(current.isLeaf())
            while(this.noOfCharacters != 0 ) {
                out.write(current.getAsciiTag());
                noOfCharacters --;
        }
        while(this.noOfCharacters != 0) {
            c = bitInStream.read();
            if (c == 0)
                current = current.getLeft();
            else if (c == 1)
                current = current.getRight();
            if (current.isLeaf()) {
                out.write(current.getAsciiTag());
                current = root;
                this.noOfCharacters --;
            }
        }
    }
}
