package org.nirvana.huffman.Compression;

import org.nirvana.utils.Frequencies;
import org.nirvana.utils.TreeNode;

import javax.naming.SizeLimitExceededException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

import static org.nirvana.utils.Frequencies.CHARACTER_TABLE_SIZE;


public class Encoder {

    PriorityQueue<TreeNode> pq;

    int numberOfCharactersRead;

    public int[] getFreqTable() {return freqTable;}

    int[] freqTable;

    public Encoder() {
        this.pq = new PriorityQueue<>(new TreeNodeComparator());
        this.numberOfCharactersRead = 0;
    }

    /**
     * Initialises a Priority Queue with TreeNodes
     * @param in InputStream
     * @throws SizeLimitExceededException when increases beyond capacity of Integer
     * @throws IOException if I/O error occurs
     */
    public PriorityQueue<TreeNode> Init_Queue(InputStream in) throws SizeLimitExceededException, IOException {
        Frequencies freq = FileContent2FreqTable(in);
        freqTable = freq.getFreqArray();

        for (int i = 0; i < CHARACTER_TABLE_SIZE; i++) {
            int f = freq.getFrequency(i);
            if (f == 0)
                continue;
            TreeNode leaf = new TreeNode(i, f);
            pq.add(leaf);
        }
        return this.pq;
    }

    /**
     * Creates a Frequency Table by reading into a file
     * @param in InputStream
     * @return freqArray ie Array of 256 character denoting the frequency of occurrence of each character
     * @throws IOException  if I/O error Occurs
     * @throws SizeLimitExceededException   if Frequency goes beyond capacity of Integer
     */
    Frequencies FileContent2FreqTable(InputStream in) throws IOException, SizeLimitExceededException {
        Frequencies freqArray = new Frequencies();
        int c;
        while ((c = in.read()) != -1) {
            numberOfCharactersRead++;
            freqArray.increment((char) c);
        }
        return freqArray;
    }

    /**
     * Creates a Huffman tree from the priority queue until only one tree remains
     *
     * @return hr Huffman tree root
     */
    public TreeNode createBinaryTree()  {

        if (this.pq.size() == 0) return null;
        while (this.pq.size() != 1) {
            TreeNode innerNode = new TreeNode(this.pq.remove(), this.pq.remove());
            pq.add(innerNode);
        }
        return pq.remove();
    }

    public int getNumberOfCharactersRead() {
        return this.numberOfCharactersRead;
    }


    /**
     * Comparator for Priority Queue
     */
    static class TreeNodeComparator implements Comparator<TreeNode> {

        /* Compares two TreeNode objects
         */
        @Override
        public int compare(TreeNode o1, TreeNode o2) {
            if (o1.getFrequency() == o2.getFrequency()) {
                if (o1.getAsciiTag() == -1 && o2.getAsciiTag() != -1)
                    return 1;
                else if ((o1.getAsciiTag() != -1 && o2.getAsciiTag() == -1))
                    return -1;
                if (o1.getAsciiTag() > o2.getAsciiTag())
                    return 1;
                else
                    return -1;
            } else
                return o1.getFrequency() - o2.getFrequency();
        }
    }
}
