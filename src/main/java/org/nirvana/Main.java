package org.nirvana;


import org.nirvana.huffmanstatic.Compression.Encoder;
import org.nirvana.huffmanstatic.Compression.FileEncoderHandler;
import org.nirvana.huffmanstatic.Decompression.FileDecoderHandler;
import org.nirvana.huffmanstatic.HuffmanCodeTree;
import org.nirvana.huffmanwords.TopNFrequencies;
import org.nirvana.huffmanwords.compression.WordCodeFileEncodeHandler;
import org.nirvana.huffmanwords.compression.WordEncoder;
import org.nirvana.huffmanwords.decompression.WordCodeFileDecoderHandler;
import org.nirvana.huffmanwords.WordHuffmanCodeTree;
import org.nirvana.utils.TreeNode;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.logging.*;

public class Main {

    private static Logger LOGGER = Logger.getLogger(Main.class.getName());
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT.%1$tL] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Main.class.getName());

    }
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
        LOGGER.setLevel(Level.CONFIG);

        Runtime runtime = Runtime.getRuntime();
        final double[] bitsOnFreq = {0};

        //check if minimum number of arguments are available for the program
        if (args.length < 3) {
            System.err.println("Usage: java HuffmanCompress -option input_file output_file");
            System.exit(1);
            return;
        }
        File ipFile = new File(args[args.length - 2]);
        File opFile = new File(args[args.length - 1]);
        if (!ipFile.exists()) throw new FileNotFoundException("ERROR: File Not Found");
        if (opFile.exists())  throw new FileAlreadyExistsException("ERROR: File Already Exists");

        LOGGER.log(Level.INFO,"Execution Started");
        if (args[0].equals("-c") || args[0].equals("--compress")){
            if(args[1].equals("-s") || args[1].equals("--static")) {
                FileEncoderHandler fileHandler;
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {

                        Encoder encoder = new Encoder();
                        encoder.Init_Queue(bufIn);
                        HuffmanCodeTree codeTree = new HuffmanCodeTree(encoder.buildHuffmanTree());
                        LOGGER.log(Level.INFO,"Huffman Tree build Completed. Huffman Codes Generated");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                bitsOnFreq[0] = codeTree.avgCodeLength(encoder.getFreqTable());
                                LOGGER.log(Level.INFO,"A Thread ran to finish. Calculated Average Bits per Character");
                            }
                        }).start();
                        fileHandler = new FileEncoderHandler(encoder.getNumberOfCharactersRead(), outBuf);
                        BufferedInputStream reader2 = new BufferedInputStream(new FileInputStream(ipFile));
                        fileHandler.writeText(codeTree.getBinaryTreeAsBitString(), codeTree.getHuffmanCodeSet(), reader2);
                    }
                }
            }
            else if(args[1].equals("-w") || args[1].equals("--word"))
            {
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                        WordEncoder woe = new WordEncoder();
                        HashMap<String,Integer> freqMap =(HashMap<String, Integer>) woe.generateFrequencyMap(bufIn);
                        LOGGER.info("Frequency Map Generated");
                        //
                        TopNFrequencies topN = new TopNFrequencies();
                        //double newSplitPercent = topN.calculateIdealSplit((HashMap<String, Integer>) freqMap);

                        TopNFrequencies.GroupForPercent newSplitObj = topN.mapReduceJob4BestPercent(freqMap,3);
                        double newSplitPercent = newSplitObj.percent;
                        HashMap<String,Integer> splitMap = (HashMap<String, Integer>) newSplitObj.frequency;
                        //double newSplitPercent = topN.mapReduceJob4BestPercent(freqMap,4);

                        //HashMap<String, Integer> splitMap = (HashMap<String, Integer>) topN.convert2TopN(freqMap,newSplitPercent);
                        LOGGER.log(Level.INFO,"New Frequency HashMap Generated. Chosen {0} as Split Percentage ",newSplitPercent*100);


                        WordEncoder wordEncoder = new WordEncoder(splitMap);
                        wordEncoder.initQueue(splitMap);
                        TreeNode tr = wordEncoder.buildHuffmanTree();
                        LOGGER.log(Level.INFO,"Huffman Tree build Completed");

                        WordHuffmanCodeTree wodHuffmanCodeTree = new WordHuffmanCodeTree(tr);
                        HashMap<String, String> codeSet = wodHuffmanCodeTree.getHuffmanCodeSet();

                        LOGGER.log(Level.INFO,"Huffman Code Set Generation Completed");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                bitsOnFreq[0] = wodHuffmanCodeTree.avgCodeLength(splitMap);
                                LOGGER.log(Level.INFO,"Thread:{0} A Thread ran to finish. Calculated Average Bits per Character",Thread.currentThread().threadId());
                            }
                        }).start();

                        LOGGER.log(Level.INFO,"Opening new Stream for Encoding");
                        WordCodeFileEncodeHandler fileEncodeHandler = new WordCodeFileEncodeHandler(wordEncoder.getNoOfWords(), outBuf);
                        BufferedInputStream buf2 = new BufferedInputStream(new FileInputStream(ipFile));

                        fileEncodeHandler.writeText(splitMap, codeSet, buf2);


                    }
                }

            }
            LOGGER.log(Level.INFO,"Write Operation Completed. Output File Generated");
            System.out.println("\nCompression Operation Completed...");

        }
        else if (args[0].equals("-d") || args[0].equals("--decompress")) {
            if(args[1].equals("-s") || args[1].equals("--static")) {
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                        FileDecoderHandler fileHandler = new FileDecoderHandler(bufIn);
                        int lenOfBitString = fileHandler.readHeader();
                        LOGGER.log(Level.INFO,"Header Information Recovered.");
                        String bitString = fileHandler.getBitTree2String(lenOfBitString);
                        TreeNode tree = fileHandler.BitString2BinaryTree(bitString);
                        LOGGER.log(Level.INFO,"Header Information Processed. Beginning to write to File");

                        fileHandler.writeText(outBuf, tree);
                    }
                }
            } else if ((args[1].equals("-w") || args[1].equals("--word"))) {
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                        WordCodeFileDecoderHandler fileDecoder = new WordCodeFileDecoderHandler(bufIn);

                        HashMap<String, Integer> freqMap = fileDecoder.readHeader();
                        LOGGER.log(Level.INFO, "Header Information Recovered.");
                        //
                        WordEncoder wordEncoder = new WordEncoder(freqMap);
                        wordEncoder.initQueue(freqMap);
                        TreeNode root = wordEncoder.buildHuffmanTree();

                        LOGGER.log(Level.INFO, "Header Information Processed. Beginning to write to File");
                        fileDecoder.writeText(outBuf, root);

                    }
                }
            }
            LOGGER.log(Level.INFO,"Finished Writing to Output File");
            System.out.println("\nDecompression Operation Completed...");

        }

        long finish = System.currentTimeMillis();

        if (args[2].equals("-a") || args[2].equals("--all")) {

            System.out.println("Time Elapsed          : " + (finish - start) + " ms\n");
            System.out.println("Input  file size    was " + ipFile.length() + " b");
            System.out.println("Output file size now is " + opFile.length() + " b");
            System.out.println("Memory Allocated      : " + (runtime.totalMemory()/(1024*1024)) + "Mb");
            System.out.println("Memory Used           : " + ((runtime.totalMemory() - runtime.freeMemory())/(1024*1024)) + "Mb");
            float sizeIn = Integer.parseInt("" + ipFile.length());
            float sizeOut = Integer.parseInt("" + opFile.length());
            if (args[0].equals("-c") || args[0].equals("--compress")) {
                System.out.printf("Compression Ratio  : %.5f%n",(sizeIn/ sizeOut));
                System.out.printf("Space Saving       : %.5f%n",(1-(sizeOut/sizeIn)));
                System.out.println("Average Code Length: " + (float) bitsOnFreq[0] + " bits/symbol");
                System.out.printf("Compressed File size is now %s %% of Original File%n", sizeOut / sizeIn * 100);
            }
        }
    }
}