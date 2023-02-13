package org.nirvana.utils;

import javax.naming.SizeLimitExceededException;
import java.util.Optional;

public interface FrequencyMap {
    int getFrequency(int index);
    void increment(int index) throws SizeLimitExceededException;
}
