package org.nirvana.utils;

import javax.naming.SizeLimitExceededException;
import java.util.Arrays;

public class Frequencies implements FrequencyMap<Integer>{
    int[] freqArray;
    public static int CHARACTER_TABLE_SIZE = 256;
    
    public Frequencies() {
        freqArray = new int[CHARACTER_TABLE_SIZE];
        Arrays.fill(freqArray, 0);
    }
    public int[] getFreqArray() {return freqArray;}


    @Override
    public int getFrequency(Integer index) {
        isCharacter(index);
        return freqArray[index];
    }


    @Override
    public void increment(Integer ascii_value) throws SizeLimitExceededException {
        if(this.isCharacter(ascii_value))
            if(freqArray[ascii_value] == Integer.MAX_VALUE)
                throw new SizeLimitExceededException("ERROR: Maximum Limit Reached");
            freqArray[ascii_value] += 1;
    }

    /**
     * Check whether input parameter is a valid character
     *
     * @param ascii_value character
     * @return True if it is valid else False
     */
    private boolean isCharacter(int ascii_value) {
        if(ascii_value >=0 && 255 >= ascii_value)
            return true;
        throw new IllegalArgumentException("ERROR: Not ASCII value");
    }
}
