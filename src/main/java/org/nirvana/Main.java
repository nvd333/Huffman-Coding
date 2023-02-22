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

public class Main {
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        double bitsOnFreq = 0;

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

        if (args[0].equals("-c") || args[0].equals("--compress")){
            if(args[1].equals("-s") || args[1].equals("--static")) {
                FileEncoderHandler fileHandler;
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {

                        Encoder encoder = new Encoder();
                        encoder.Init_Queue(bufIn);
                        HuffmanCodeTree codeTree = new HuffmanCodeTree(encoder.buildHuffmanTree());
                        bitsOnFreq = codeTree.avgCodeLength(encoder.getFreqTable());

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
                        //
                        TopNFrequencies topN = new TopNFrequencies();
                        double splitPercent = topN.calculateIdealSplit((HashMap<String, Integer>) freqMap);
                        System.out.printf("Split Percent  : %.5f\n",splitPercent);

                        HashMap<String, Integer> splitMap = (HashMap<String, Integer>) topN.convert2TopN(freqMap,splitPercent);
                        //HashMap<?,Integer> hmap = (HashMap<String,Integer>) woe.generateFrequencyMap(bufIn);
                        WordEncoder wordEncoder = new WordEncoder(splitMap);
                        wordEncoder.initQueue(splitMap);
                        TreeNode tr = wordEncoder.buildHuffmanTree();

                        WordHuffmanCodeTree wodHuffmanCodeTree = new WordHuffmanCodeTree(tr);
                        HashMap<String, String> a = wodHuffmanCodeTree.getHuffmanCodeSet();
                        bitsOnFreq = wodHuffmanCodeTree.avgCodeLength(splitMap);
                        WordCodeFileEncodeHandler fileEncodeHandler = new WordCodeFileEncodeHandler(wordEncoder.getNoOfWords(), outBuf);
                        BufferedInputStream buf2 = new BufferedInputStream(new FileInputStream(ipFile));

                        //fileEncodeHandler.writeText(wordHuffmanCodeTree.HuffTreeAsBitString(tr), a, buf2);
//woe.getFreqMapAsBitString()
                        fileEncodeHandler.writeText(WordEncoder.getFreqMapAsBitString(splitMap), a, buf2);

                    }
                }

            }

            System.out.println("\nCompression Operation Completed...");

        }
        else if (args[0].equals("-d") || args[0].equals("--decompress")) {
            if(args[1].equals("-s") || args[1].equals("--static")) {
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                        FileDecoderHandler fileHandler = new FileDecoderHandler(bufIn);

                        int lenOfBitString = fileHandler.readHeader();
                        String bitString = fileHandler.getBitTree2String(lenOfBitString);
                        TreeNode tree = fileHandler.BitString2BinaryTree(bitString);

                        fileHandler.writeText(outBuf, tree);
                    }
                }
            } else if ((args[1].equals("-w") || args[1].equals("--word"))) {
                try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                    try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                        WordCodeFileDecoderHandler fileDecoder = new WordCodeFileDecoderHandler(bufIn);

                        int bitStrLen = fileDecoder.readHeader();
                        HashMap<String,Integer> freqMap = fileDecoder.getBitTree2FrequencyMap(bitStrLen);

                        //
                        WordEncoder wordEncoder = new WordEncoder(freqMap);
                        wordEncoder.initQueue(freqMap);
                        TreeNode root = wordEncoder.buildHuffmanTree();
                        //TreeNode root =fileDecoder.getBitTree2BinaryTree(bitStrLen);
                        fileDecoder.writeText(outBuf,root);
                    }
                }
            }
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
                System.out.printf("Compression Ratio  : %.5f\n",(sizeIn/ sizeOut));
                System.out.printf("Space Saving       : %.5f\n",(1-(sizeOut/sizeIn)));
                System.out.println("Average Code Length: " + (float)bitsOnFreq + " bits/symbol");
                System.out.printf("Compressed File size is now %s %% of Original File", sizeOut / sizeIn * 100);
            }


        }
    }
}