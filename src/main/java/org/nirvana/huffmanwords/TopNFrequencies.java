package org.nirvana.huffmanwords;

import org.nirvana.huffmanwords.compression.WordEncoder;
import org.nirvana.utils.TreeNode;

import javax.naming.SizeLimitExceededException;
import java.io.IOException;
import java.util.*;

public class TopNFrequencies{
    
    
    public HashMap<?, Integer> convert2TopN(HashMap<?,Integer> original,double percent)
    {
        ArrayList<String> keySet = new ArrayList(original.keySet());
        Collections.sort(keySet, Comparator.comparing(s -> -original.get(s)));
        int topPercent = (int) (percent* keySet.size());
        Map<String, Integer> topNMap = new LinkedHashMap<>();
        for(int i = 0;i< topPercent;i++)
            topNMap.put(keySet.get(0),original.get(keySet.remove(0)));
        for(String str:keySet) {

            try {
                int asciiChar = Integer.parseInt(str);
                topNMap.put(String.valueOf(asciiChar), topNMap.getOrDefault(String.valueOf(asciiChar),0)+original.get(str));
            }
            catch (NumberFormatException isWord)
            {
                for(char c:str.toCharArray())
                        topNMap.put(String.valueOf(c), topNMap.getOrDefault(String.valueOf(c),0)+original.get(str));
            }
        }
        return (HashMap<?, Integer>) topNMap;

    }

    public double calculateIdealSplit(HashMap<String,Integer> original) throws SizeLimitExceededException, IOException {
        double[] best = new double[]{0.0,Integer.MAX_VALUE};
        double[] currentState = new double[]{0.0,Integer.MAX_VALUE};

        //TopNFrequencies topNFrequencies = new TopNFrequencies();
        double temp = 10000;
        double coolingRate = 0.2;

        while(temp>1)
        {
            double newPercent = Math.random();
            HashMap<String,Integer> newFreqMap = (HashMap<String, Integer>) this.convert2TopN(original,newPercent);

            double totalLen = this.calculateBodyLength((HashMap<String, Integer>) newFreqMap.clone());
            if(acceptanceProbability(currentState[1], totalLen, temp) > Math.random()) {
                currentState[0] = newPercent;
                currentState[1] = totalLen;
            }
            if(currentState[1] < best[1]) {
                best = currentState;
            }
            temp = temp / (1 + coolingRate * temp);
        }
        //topNFrequencies = null;
        System.out.println(best[1]);
        return best[0];
    }

    private double acceptanceProbability(double energy,double newEnergy,double temp){
        if(newEnergy<energy)
            return 1.0;
        return Math.exp((energy - newEnergy) / temp);
    }

    private double calculateBodyLength(HashMap<String,Integer> newFreqMap) throws IOException {


        WordEncoder wordEncoder = new WordEncoder(newFreqMap);
        wordEncoder.initQueue(newFreqMap);
//        PriorityQueue<TreeNode> pq = new PriorityQueue<>(new Comparator<TreeNode>() {
//            @Override
//            public int compare(TreeNode o1, TreeNode o2) {
//                if(o1.getFrequency() == o2.getFrequency())
//                    return o1.getWordLabel().compareTo(o2.getWordLabel());
//                return  o1.getFrequency() - o2.getFrequency();
//            }
//        });
//        for(String word: newFreqMap.keySet())
//        {
//            int freq = newFreqMap.get(word);
//            //this.noOfWords +=freq;
//            TreeNode leaf = new TreeNode(word,freq);
//            pq.add(leaf);
//        }
//        while(pq.size()>1) {
//            TreeNode left = pq.poll();
//            TreeNode right = pq.poll();
//            pq.add(new TreeNode(left,right));
//        }
//        TreeNode tr = pq.poll();

        TreeNode tr =  wordEncoder.buildHuffmanTree();
        //TreeNode root = new TreeNode(null,null);
        WordHuffmanCodeTree wordHuffmanCodeTree = new WordHuffmanCodeTree(tr);
        HashMap<String, String> codeSet = wordHuffmanCodeTree.getHuffmanCodeSet();
        double bodyLen = 0;
        for(String word: codeSet.keySet())
        {
            bodyLen+=codeSet.get(word).length()*newFreqMap.get(word);
        }
        double headLen = wordEncoder.getFreqMapAsBitString(newFreqMap).split(",").length+8.0;
        double totalLen = headLen + bodyLen/8.0;
        return totalLen;

    }


}
