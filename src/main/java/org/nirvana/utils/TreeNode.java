package org.nirvana.utils;

public class TreeNode {

    static int uniqueID = 0;


    int freq;      // always positive
    String label;      // ASCII value corresponding to character
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
        this.label = ""+ascii_label;
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
        this.label = String.valueOf(-++uniqueID);
        this.left = left;
        this.right = right;

        if (this.left == null) this.freq = 0;
        else if (this.right == null) this.freq = left.getFrequency();
        else this.freq = left.freq + right.freq;
    }

    public TreeNode(String word,int freq)
    {
        this.label = word;
        this.freq = freq;
        this.left = this.right = null;
    }

    public String getWordLabel(){ return this.label;}
    public int getFrequency() {
        return this.freq;
    }
    public int getAsciiTag() {return Integer.parseInt(label);}
    public TreeNode getRight(){ return  this.right;}
    public TreeNode getLeft(){ return  this.left;}
    public boolean isLeaf() {
        return (this.right == null && this.left == null);
    }
    public void setLeftBranch(TreeNode left) {this.left = left;        if(left!=null) this.label = ""+-30;}
    public void setRightBranch(TreeNode right){this.right = right;     if(left!=null) this.label = ""+-30;}
}
