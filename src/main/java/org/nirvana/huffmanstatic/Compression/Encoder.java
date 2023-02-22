package org.nirvana.huffmanstatic.Compression;

import org.nirvana.baseHuffman.HuffmanBaseEncoder;
import org.nirvana.huffmanstatic.Frequencies;
import org.nirvana.utils.TreeNode;

import javax.naming.SizeLimitExceededException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.PriorityQueue;

import static org.nirvana.huffmanstatic.Frequencies.CHARACTER_TABLE_SIZE;


public class Encoder implements HuffmanBaseEncoder {

    PriorityQueue<TreeNode> pq;

    int numberOfCharactersRead;

    int[] freqTable;

    public int[] getFreqTable() {return freqTable;}

    public Encoder() {
        this.pq = new PriorityQueue<>(new TreeNodeComparator());
        this.numberOfCharactersRead = 0;
    }

    public Encoder(int[] freqTable)
    {
        this.pq = new PriorityQueue<>(new TreeNodeComparator());
        this.freqTable = freqTable;
        this.numberOfCharactersRead = 0;
        for(int frequency:freqTable)
            this.numberOfCharactersRead+=frequency;
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
            freqArray.increment(c);
        }
        return freqArray;
    }

    /**
     * Creates a Huffman tree from the priority queue until only one tree remains
     *
     * @return hr Huffman tree root
     */
    @Override
    public TreeNode buildHuffmanTree()  {

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

        /**
         *  Compares two TreeNode objects
         */
        @Override
        public int compare(TreeNode o1, TreeNode o2) {
            if (o1.getFrequency() == o2.getFrequency())
                return Integer.compare(o1.getAsciiTag(),o2.getAsciiTag());
            return o1.getFrequency() - o2.getFrequency();
        }
    }
}
