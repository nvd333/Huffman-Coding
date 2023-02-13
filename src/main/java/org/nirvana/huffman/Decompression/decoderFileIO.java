package org.nirvana.huffman.Decompression;

import org.nirvana.utils.TreeNode;

import java.io.OutputStream;

public interface decoderFileIO {

    void writeText(OutputStream out, TreeNode node) throws Exception;
}
