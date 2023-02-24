package org.nirvana.huffmanwords;


import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.collections4.ListUtils;
import org.nirvana.Main;
import org.nirvana.huffmanwords.compression.WordEncoder;
import org.nirvana.utils.TreeNode;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;


public class TopNFrequencies{
    
    
    static List<String> keySet1;
    private static Logger LOGGER = Logger.getLogger(Main.class.getName());
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT.%1$tL] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Main.class.getName());
    }
    
    
    private HashMap<?, Integer> convert2TopN(HashMap<?,Integer> original, double percent)
    {
        List<String> keySet = new ArrayList<>(keySet1);
//        List<String> keySet = (List<String>) new ArrayList<>(original.keySet());
//        Collections.sort(keySet, Comparator.comparing(s -> -original.get(s)));
        int topPercent = (int) (percent* keySet.size());
        Map<String, Integer> topNMap = new LinkedHashMap<>();
        for(int i = 0;i< topPercent;i++)
            topNMap.put(keySet.get(0),original.get(keySet.remove(0)));
        for(String str:keySet) {
            if(NumberUtils.isParsable(str))
            {
                int asciiChar = Integer.parseInt(str);
                topNMap.put(String.valueOf(asciiChar), topNMap.getOrDefault(String.valueOf(asciiChar),0)+original.get(str));
            }
            else
            {
                for(char c:str.toCharArray())
                        topNMap.put(String.valueOf(c), topNMap.getOrDefault(String.valueOf(c),0)+original.get(str));
            }
        }
        return (HashMap<?, Integer>) topNMap;

    }

    public double calculateIdealSplit(HashMap<String,Integer> original) throws IOException {
        double[] best = new double[]{0.0,Integer.MAX_VALUE};
        double[] currentState = new double[]{0.0,Integer.MAX_VALUE};
        //Map<String,Integer> bestFreqMap = null;

        double temp = 10000;
        double coolingRate = 0.33;

        while(temp>1)
        {
            double newPercent = Math.random();
            HashMap<String,Integer> newFreqMap = (HashMap<String, Integer>) this.convert2TopN(original,newPercent);

            double totalLen = this.calculateTotalLength((HashMap<String, Integer>) newFreqMap.clone());
            if(acceptanceProbability(currentState[1], totalLen, temp) > Math.random()) {
                currentState[0] = newPercent;
                currentState[1] = totalLen;
            }
            if(currentState[1] < best[1]) {
                best = currentState;
                //bestFreqMap = (Map<String, Integer>) newFreqMap.clone();
            }
            temp = temp / (1 + coolingRate * temp);
        }
        return best[0];
    }

    private double acceptanceProbability(double energy,double newEnergy,double temp){
        if(newEnergy<energy)
            return 1.0;
        return Math.exp((energy - newEnergy) / temp);
    }

    private double calculateTotalLength(HashMap<String,Integer> newFreqMap) throws IOException {


        WordEncoder wordEncoder = new WordEncoder(newFreqMap);
        wordEncoder.initQueue(newFreqMap);
        TreeNode tr =  wordEncoder.buildHuffmanTree();
        WordHuffmanCodeTree wordHuffmanCodeTree = new WordHuffmanCodeTree(tr);
        HashMap<String, String> codeSet = wordHuffmanCodeTree.getHuffmanCodeSet();
        double bodyLen = 0;
        for(Map.Entry<String,String> word: codeSet.entrySet())
            bodyLen+=word.getValue().length()*newFreqMap.get(word.getKey());

        double headLen = (SerializationUtils.serialize(newFreqMap).length+4.0);
        double totalLen = headLen + bodyLen/8.0;
        return totalLen;

    }


    public GroupForPercent TaskForPercentJob(HashMap<String,Integer> original, List<Double> temperatures) throws IOException {
        GroupForPercent best  = new GroupForPercent();
        GroupForPercent currentState = new GroupForPercent();
        best.set(0.0,Integer.MAX_VALUE);
        currentState.clone(best);

        int i =0;
        while( i != temperatures.size() )
        {
            double newPercent = Math.random();
            HashMap<String,Integer> newFreqMap = (HashMap<String, Integer>) this.convert2TopN(original,newPercent);

            double totalLen = this.calculateTotalLength((HashMap<String, Integer>) newFreqMap.clone());
            if(acceptanceProbability(currentState.length, totalLen, temperatures.get(i)) > Math.random()) {
                currentState.set(newPercent,totalLen,newFreqMap);
            }
            if(currentState.length < best.length) {
                best.clone(currentState);
            }
            i++;
        }
        return best;
    }


    public GroupForPercent mapReduceJob4BestPercent(HashMap<String,Integer> original,int N) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        //TopNFrequencies task2 = new TopNFrequencies();

        double temp = 10000;
        double coolRate = 0.33;
        keySet1 = (List<String>) new ArrayList<>(original.keySet());
        Collections.sort(keySet1, Comparator.comparing(s -> -original.get(s)));

        ArrayList<Double> tempFullSet = new ArrayList<>();
        while(temp > 1)
        {
            tempFullSet.add(temp);
            temp = temp / (1 + coolRate*temp);
        }
        List<List<Double>> splitList = ListUtils.partition(tempFullSet,tempFullSet.size()/N ==0 ? 2 : tempFullSet.size()/N );

        ArrayList<Future<GroupForPercent>> freqContendersObj = new ArrayList<>();
        for(int k =0; k<splitList.size(); k++)
        {
            int finalK = k;
            freqContendersObj.add(threadPoolExecutor.submit(() -> {
                    long threadId = Thread.currentThread().threadId();
                    LOGGER.info("Thread:"+threadId+" started.Currently Running for "+splitList.get(finalK));
                    return this.TaskForPercentJob(original,splitList.get(finalK));
            }));
        }
        ArrayList<GroupForPercent> unsafeList = new ArrayList<>();
        List<GroupForPercent> bestFreqContenders = Collections.synchronizedList(unsafeList);
        for (Future<GroupForPercent> groupForPercentFuture : freqContendersObj)
            bestFreqContenders.add(groupForPercentFuture.get());
        Collections.sort(bestFreqContenders,Comparator.comparing(s -> s.length));

        threadPoolExecutor.shutdown();
        return (bestFreqContenders.get(0));
    }


    public static class GroupForPercent {
        public double percent;
        public double length;
        public HashMap<?,Integer> frequency;

        public void set(double percent,double length,HashMap<?,Integer> frequencyMap){
            this.percent = percent;
            this.length = length;
            this.frequency = (HashMap<?, Integer>) frequencyMap.clone();
        }
        public void set(double percent,double length){
            this.percent = percent;
            this.length = length;
            this.frequency = null;
        }
        public void clone(GroupForPercent obj)
        {
            this.percent = obj.percent;
            this.length = obj.length;
            this.frequency = obj.frequency == null ?null:(HashMap<?, Integer>) obj.frequency.clone();
        }
    }
}
