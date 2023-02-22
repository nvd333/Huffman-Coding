package org.nirvana.huffmanwords;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class TopNFrequenciesTest {

    @Test
    public void convert2TopN() {
        HashMap<String,Integer> hmap = new HashMap<>();
        hmap.put("hello",1);
        hmap.put("amls",1);
        hmap.put("zsda",2);
        hmap.put("10",4);

        TopNFrequencies n = new TopNFrequencies();
        //HashMap<String,Integer> lhm = (HashMap<String, Integer>) n.convert2TopN(hmap);
        //System.out.println(lhm);
     }
}