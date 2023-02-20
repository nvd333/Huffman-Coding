package org.nirvana.huffmanwords;

import java.util.*;

public class TopNFrequencies{
    
    
    public HashMap<?, Integer> convert2TopN(HashMap<?,Integer> original)
    {
        ArrayList<String> keySet = new ArrayList(original.keySet());
        Collections.sort(keySet, Comparator.comparing(s -> -original.get(s)));
        int topPercent = (int) (0.33* keySet.size());
        Map<String, Integer> topNMap = new LinkedHashMap<>();
        for(int i = 0;i< topPercent;i++)
            topNMap.put(keySet.get(0),original.get(keySet.remove(0)));
        for(String str:keySet) {
//            if(str.length() > 5 )
//            {
//                topNMap.put(str,topNMap.getOrDefault())
//            }
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


}
