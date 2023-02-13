package org.nirvana.utils;

public class TreeNode {

    static int uniqueID = 0;


    int freq;      // always positive
    int label;      // ASCII value corresponding to character
    private TreeNode left;
    private TreeNode right;

    /**
     * Constructor
     * Creates a Leaf node
     *
     * @param ascii_label character value
     * @param frequency occurrence of this character
     */
    public TreeNode(int ascii_label,int frequency)
    {
        this.freq = frequency;
        this.label = ascii_label;
        this.left = null;
        this.right = null;
    }

    /**
     * Constructor
     * Creates a Inner Node
     *
     * @param left left Child Node
     * @param right right Child Node
     */
    public TreeNode(TreeNode left,TreeNode right)
    {
        this.label = -700-uniqueID++;
        this.left = left;
        this.right = right;

        if (this.left == null) this.freq = 0;
        else if (this.right == null) this.freq = left.getFrequency();
        else this.freq = left.freq + right.freq;
    }

    public int getFrequency() {
        return this.freq;
    }
    public int getAsciiTag() {return label;}
    public TreeNode getRight(){ return  this.right;}
    public TreeNode getLeft(){ return  this.left;}
    public boolean isLeaf() {
        return (this.right == null && this.left == null)  && label>=0;
    }
    public void setLeftBranch(TreeNode left) {this.left = left;        this.label = -300;}
    public void setRightBranch(TreeNode right){this.right = right;        this.label = -300;}
}
