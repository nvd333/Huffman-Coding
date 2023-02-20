package org.nirvana.huffmanstatic.Compression;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.nirvana.utils.CodeTree;
import org.nirvana.utils.TreeNode;

import javax.naming.SizeLimitExceededException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class EncoderTest {

    Encoder encoder;
    ByteArrayInputStream byteStream;


    @Test(expected = NullPointerException.class)
    public void init_Queue_WhenStreamIsNull() throws SizeLimitExceededException, IOException {
        encoder = new Encoder();
        encoder.Init_Queue(null);
    }

    @ParameterizedTest
    @CsvSource({"NormalString,N1 S1 a1 g1 i1 l1 m1 n1 o1 r2 t1  ",
                "Password is $%|*77*|,$1 d1 %1 i1 o1 P1 a1 *2  2 72 r1 s3 w1 |2 "})
    public void init_Queue_WhenStreamIsPresent(String input,String expected) throws SizeLimitExceededException, IOException {
        encoder = new Encoder();
        byteStream = new ByteArrayInputStream(input.getBytes());
        PriorityQueue<TreeNode> pq = encoder.Init_Queue(byteStream);
        var Object = new Object() {
            String actual = "";
        };
        pq.forEach(S->{
            Object.actual +=(char)S.getAsciiTag();
            Object.actual += S.getFrequency() +" ";});
        Assert.assertEquals(expected+" ",Object.actual);
    }

    @Test
    public void createBinaryTree_WhenQueueIsNull() throws SizeLimitExceededException, IOException {
        encoder = new Encoder();
        byteStream = new ByteArrayInputStream("".getBytes());
        encoder.Init_Queue(byteStream);
        assertNull(encoder.buildHuffmanTree());
    }

    @ParameterizedTest
    @CsvSource({"A,A(1)","Ann,*(3) ├──A(1) └──n(2)"})
    public void createBinaryTree_WithSingleNode(String Input,String expected) throws SizeLimitExceededException, IOException {
        encoder = new Encoder();
        byteStream = new ByteArrayInputStream(Input.getBytes());
        encoder.Init_Queue(byteStream);
        Assert.assertEquals(expected, CodeTree.traversePreOrder(encoder.buildHuffmanTree()).replace('\n',' '));
    }
}