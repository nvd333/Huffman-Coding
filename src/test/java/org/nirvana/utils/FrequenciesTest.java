package org.nirvana.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.naming.SizeLimitExceededException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class FrequenciesTest {
    static Frequencies frequencies;
    @Parameterized.Parameter(0)
    public int character;
    @Parameterized.Parameter(1)
    public int expected;
    @Parameterized.Parameters
    public static List<Object[]> data() {
        Object[][] data = new Object[][]{{65,1},{84,1},{65,2},{65,3}};
        return Arrays.asList(data);
    }
    @BeforeClass
    public static void SetUp(){
        frequencies = new Frequencies();
    }

    @Test
    public void increment_WhenIncrementBy1() throws SizeLimitExceededException {
        frequencies.increment(character);
        Assert.assertEquals(expected,frequencies.getFrequency(character));
    }
}