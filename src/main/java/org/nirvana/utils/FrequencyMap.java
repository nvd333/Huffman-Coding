package org.nirvana.utils;

import javax.naming.SizeLimitExceededException;

public interface FrequencyMap<T> {
    int getFrequency(T index);
    void increment(T index) throws SizeLimitExceededException;
}
