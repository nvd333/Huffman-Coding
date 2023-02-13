package org.nirvana.utils;

public interface CodeTree {
    String HuffTreeAsBitString(TreeNode root);
    String getHuffCode(int character);

    /**
     * Generate a string representation of the Huffman tree
     *
     * @param root Huffman tree node
     * @return string representation of the Huffman tree
     */

    static String traversePreOrder(TreeNode root) {

        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getLabelling(root.label) + "(" + root.freq + ")");

        String pointerRight = "└──";
        String pointerLeft = (root.getRight() != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.getLeft(), root.getRight() != null);
        traverseNodes(sb, "", pointerRight, root.getRight(), false);

        return sb.toString();
    }

    /**
     * Helper function to traverse the tree nodes and print the labels and frequencies
     *
     * @param sb      StringBuilder to append each node label and frequency
     * @param padding use to distinguish relationship between nodes
     * @param pointer
     * @param node
     */
    private static void traverseNodes(StringBuilder sb, String padding, String pointer, TreeNode node,
                              boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(getLabelling(node.label)+ "(" + node.freq + ")");

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, node.getLeft(), node.getRight() != null);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getRight(), false);
        }
    }

    private static String getLabelling(int label){
        if(label>=0 && label<=255)
            return ""+(char)label;
        return "*";
    }



}
