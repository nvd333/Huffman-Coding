package org.nirvana.wordHuffman;


import org.nirvana.huffman.Compression.FileEncoderHandler;
import org.nirvana.utils.TreeNode;
import org.nirvana.wordHuffman.compression.WordCodeFileEncodeHandler;
import org.nirvana.wordHuffman.compression.wordEncoder;
import org.nirvana.wordHuffman.decompression.WordCodeFileDecoderHandler;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

public class WordZipApp {
    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        File ipFile = new File(args[args.length - 2]);
        File opFile = new File(args[args.length - 1]);

        if (!ipFile.exists()) throw new FileNotFoundException("ERROR: File Not Found");
        if (opFile.exists()) throw new FileAlreadyExistsException("ERROR: File Already Exists");

        if (args[0].equals("-c") || args[0].equals("--compress")) {
            FileEncoderHandler fileHandler;
            try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                    wordEncoder woe = new wordEncoder();
                    woe.initQueue((Map<String, Integer>) woe.generateFrequencyMap(bufIn),bufIn);
                    TreeNode tr = woe.buildHuffmanTree();
                    wordHuffmanCodeTree wodHuffmanCodeTree = new wordHuffmanCodeTree(tr);
                    HashMap<String, String> a = wodHuffmanCodeTree.getHuffmanCodeSet();
                    WordCodeFileEncodeHandler fileEncodeHandler = new WordCodeFileEncodeHandler(woe.getNoOfWords(), outBuf);
                    BufferedInputStream buf2 = new BufferedInputStream(new FileInputStream(ipFile));
                    //fileEncodeHandler.writeText(wordHuffmanCodeTree.HuffTreeAsBitString(tr), a, buf2);
                    fileEncodeHandler.writeText(woe.getFreqMapAsBitString(), a, buf2);

                }
            }
            System.out.println("\nCompression Operation Completed...");

        }
        else if (args[0].equals("-d") || args[0].equals("--decompress")) {
            try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                    WordCodeFileDecoderHandler fileDecoder = new WordCodeFileDecoderHandler(bufIn);
                    int bitStrLen = fileDecoder.readHeader();
                    HashMap<String,Integer> freqMap = fileDecoder.getBitTree2FrequencyMap(bitStrLen);
                    //
                    wordEncoder wordEncoder = new wordEncoder(freqMap);
                    wordEncoder.initQueue(freqMap,bufIn);
                    TreeNode root = wordEncoder.buildHuffmanTree();
                    //TreeNode root =fileDecoder.getBitTree2BinaryTree(bitStrLen);
                    fileDecoder.writeText(outBuf,root);
                }
            }
            System.out.println("\nDecompression Operation Completed...");

        }

        long finish = System.currentTimeMillis();

        if (args[1].equals("-a") || args[1].equals("--all")) {

            System.out.println("Time Elapsed: " + (finish - start) + " ms\n");
            System.out.println("Input  file size    was " + ipFile.length() + " b");
            System.out.println("Output file size now is " + opFile.length() + " b");
            System.out.println("Memory Allocated: " + (runtime.totalMemory() / (1024 * 1024)) + "Mb");
            System.out.println("Memory Used     : " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)) + "Mb");
            long sizeIn = Integer.parseInt("" + ipFile.length());
            long sizeOut = Integer.parseInt("" + opFile.length());
            if (args[0].equals("-c") || args[0].equals("--compress")) {
                //System.out.println("Average Code Length: " + (float)bitsOnFreq + " bits/symbol");
                System.out.printf("Compressed File size is now %s %% of Original File", (float) sizeOut / sizeIn * 100);
            }

        }
    }
}