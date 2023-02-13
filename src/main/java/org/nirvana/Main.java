package org.nirvana;

import org.nirvana.huffman.Compression.Encoder;
import org.nirvana.huffman.Compression.FileEncoderHandler;
import org.nirvana.huffman.Decompression.FileDecoderHandler;
import org.nirvana.huffman.HuffmanCodeTree;
import org.nirvana.utils.TreeNode;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

public class Main {
    public static void main(String[] args) throws Exception{
        long start = System.currentTimeMillis();
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

        if (args[0].equals("-c") || args[0].equals("--compress")) {
            FileEncoderHandler fileHandler;
            try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                    Encoder encoder = new Encoder();
                    encoder.Init_Queue(bufIn);
                    HuffmanCodeTree codeTree = new HuffmanCodeTree(encoder.createBinaryTree());
                    bitsOnFreq = codeTree.avgCodeLength(encoder.getFreqTable());

                    fileHandler = new FileEncoderHandler(encoder.getNumberOfCharactersRead(), outBuf);
                    BufferedInputStream reader2 = new BufferedInputStream(new FileInputStream(ipFile));
                    fileHandler.writeText(codeTree, reader2);
                }
            }
            System.out.println("\nCompression Operation Completed...");

        }
        else if (args[0].equals("-d") || args[0].equals("--decompress")) {
            try (BufferedInputStream bufIn = new BufferedInputStream(new FileInputStream(ipFile))) {
                try (BufferedOutputStream outBuf = new BufferedOutputStream(new FileOutputStream(opFile))) {
                    FileDecoderHandler fileHandler = new FileDecoderHandler(bufIn);

                    int lenOfBitString = fileHandler.readHeader();
                    String bitString = fileHandler.getBitTree2String(lenOfBitString);
                    TreeNode tree = fileHandler.BitString2BinaryTree(bitString);

                    fileHandler.writeText(outBuf, tree);
                }
            }
            System.out.println("\nDecompression Operation Completed...");

        }

        long finish = System.currentTimeMillis();

        if (args[1].equals("-a") || args[1].equals("--all")) {

            System.out.println("Time Elapsed: " + (finish - start) + " ms\n");
            System.out.println("Input  file size    was " + ipFile.length() + " b");
            System.out.println("Output file size now is " + opFile.length() + " b");
            long sizeIn = Integer.parseInt("" + ipFile.length());
            long sizeOut = Integer.parseInt("" + opFile.length());
            if (args[0].equals("-c") || args[0].equals("--compress")) {
                System.out.println("Average Code Length: " + (float)bitsOnFreq + " bits/symbol");
                System.out.printf("Compressed File size is now %s %% of Original File", (float) sizeOut / sizeIn * 100);
            }


        }
    }
}