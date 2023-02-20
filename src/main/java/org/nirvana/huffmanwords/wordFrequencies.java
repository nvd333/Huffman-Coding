package org.nirvana.huffmanwords;

import org.nirvana.utils.FrequencyMap;

import javax.naming.SizeLimitExceededException;
import java.util.HashMap;
import java.util.Map;

public class wordFrequencies implements FrequencyMap<String> {

    Map<String,Integer> wordFreqMap;
    int noOfWords;
    public wordFrequencies(){
        wordFreqMap = new HashMap<String,Integer>();
        noOfWords = 0;
    }

    @Override
    public int getFrequency(String index) {
        return wordFreqMap.get(index);
    }

    public Map<?,?> getWordFreqMap(){ return wordFreqMap;}
    public int getNoOfWords(){return noOfWords;}

    @Override
    public void increment(String index) throws SizeLimitExceededException {
        wordFreqMap.put(index,wordFreqMap.getOrDefault(index,0)+1);
        noOfWords ++;
    }
}

