package org.nirvana.huffmanwords;

import org.nirvana.utils.TreeNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class wordHuffmanCodeTree{


    HashMap<String,String> HuffmanCodeSet;


    public wordHuffmanCodeTree(TreeNode root)
    {
        HuffmanCodeSet = new HashMap<>();
        if(root == null) throw new NullPointerException("ERROR: Tree is Null");
        if(root.isLeaf())
            HuffmanCodeSet.put(root.getWordLabel(), "0");
        else
            generateHuffmanCode(root,"");
    }

    private void generateHuffmanCode(TreeNode node,String code) {
        if(node.isLeaf()) {
            HuffmanCodeSet.put(node.getWordLabel(), code);
            return;
        }
        generateHuffmanCode(node.getLeft(),code.concat("0"));
        generateHuffmanCode(node.getRight(),code.concat( "1"));
    }



    public static String HuffTreeAsBitString(TreeNode root) throws IOException {
        ByteArrayOutputStream byteOut =new ByteArrayOutputStream();
        ObjectOutputStream obj = new ObjectOutputStream(byteOut);
        serialize(root,obj);
        obj.close();
        byte[] byteArray = byteOut.toByteArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(b);
            sb.append(",");
        }

        return sb.substring(0, sb.length()-1);

    }


    public HashMap<String, String> getHuffmanCodeSet() {
        return HuffmanCodeSet;
    }


    public static void serialize(TreeNode node, ObjectOutputStream stream) throws java.io.IOException {
        if (node == null) {
            stream.writeBoolean(false);
            return;
        }
        stream.writeBoolean(true);
        stream.writeObject(node.getWordLabel());
        serialize(node.getLeft(), stream);
        serialize(node.getRight(), stream);
    }

    public static TreeNode deserialize(ObjectInputStream stream) throws java.io.IOException, ClassNotFoundException {
        boolean present = stream.readBoolean();
        if (!present) return null;

        String label = (String) stream.readObject();
        TreeNode node = new TreeNode(label,-400);
        node.setLeftBranch(deserialize(stream));
        node.setRightBranch(deserialize(stream));
        return node;
    }


    public double avgCodeLength(HashMap<String,Integer> freqMap)
    {
        double term;
        double numerator = 0;
        double denominator = 0;
        for(String word : HuffmanCodeSet.keySet()) {
            term = (HuffmanCodeSet.get(word).length() * freqMap.get(word))/word.length();
            numerator += term;
            denominator += freqMap.get(word);
        }
        return numerator/denominator;
    }
}
