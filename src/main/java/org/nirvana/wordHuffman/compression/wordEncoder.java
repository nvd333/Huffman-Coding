package org.nirvana.wordHuffman.compression;

import org.nirvana.wordHuffman.wordFrequencies;
import org.nirvana.utils.TreeNode;

import javax.naming.SizeLimitExceededException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class wordEncoder {

    Queue<TreeNode> pq;
    HashMap<String,Integer> frequencyMap;
    int noOfWords;



    public wordEncoder(){
        pq = new PriorityQueue<>(new TreeComparator());
        noOfWords = 0;
    }
    public wordEncoder(HashMap<?,?> FrequencyMap)
    {
        pq = new PriorityQueue<>(new TreeComparator());
        frequencyMap = (HashMap<String, Integer>) FrequencyMap;
        noOfWords = 0;
        for(Map.Entry map :frequencyMap.entrySet()) {
            noOfWords+=(int)map.getValue();
        }
    }
    public  int getNoOfWords(){return noOfWords;}

    public Map<?,?> generateFrequencyMap(BufferedInputStream bis) throws IOException, SizeLimitExceededException {
        wordFrequencies freqMap = new wordFrequencies();
        int c;
        StringBuilder word = new StringBuilder();
        while((c = bis.read()) != -1) {
            char asciiSymbol = (char) c;
            if(Character.isLetter(asciiSymbol))
                word.append(asciiSymbol);
            else {
                if (word.length() != 0) {
                    //
                    //word.append(asciiSymbol);
                    freqMap.increment(word.toString());
                    word.setLength(0);
                }
                //
                //else
                freqMap.increment(""+ c);
            }
        }
        if(word.length()!=0)
            freqMap.increment(String.valueOf(word));

        noOfWords = freqMap.getNoOfWords();
        this.frequencyMap = (HashMap<String, Integer>) freqMap.getWordFreqMap();
        return freqMap.getWordFreqMap();
    }

    public Queue<TreeNode> initQueue(Map<String,Integer> freqWordMap,BufferedInputStream br) throws SizeLimitExceededException, IOException {
        //Map<String,Integer> freqWordMap = (Map<String, Integer>) generateFrequencyMap(br);
        if(freqWordMap.isEmpty()) pq.add(new TreeNode(null,null));
        for(String word: freqWordMap.keySet())
        {
            int freq = freqWordMap.get(word);
            TreeNode leaf = new TreeNode(word,freq);
            pq.add(leaf);
        }
        return this.pq;
    }

    public TreeNode buildHuffmanTree() {
        while(pq.size()>1) {
            TreeNode left = pq.poll();
            TreeNode right = pq.poll();
            pq.add(new TreeNode(left,right));
        }
        return pq.poll();
    }

    public String getFreqMapAsBitString() throws IOException {
        ByteArrayOutputStream byteOut =new ByteArrayOutputStream();
        ObjectOutputStream obj = new ObjectOutputStream(byteOut);
        obj.writeObject(frequencyMap);
        obj.close();
        byte[] byteArray = byteOut.toByteArray();
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(b);
            sb.append(",");
        }
        return sb.substring(0, sb.length()-1);
    }



    private static class TreeComparator implements Comparator<TreeNode> {
        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * @param o1 the object to be compared.
         * @param o2 the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compare(TreeNode o1,TreeNode o2) {
            if(o1.getFrequency() == o2.getFrequency())
                return o1.getWordLabel().compareTo(o2.getWordLabel());
            return  o1.getFrequency() - o2.getFrequency();
        }
    }
}
