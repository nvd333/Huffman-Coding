package org.nirvana.BitIO;

import java.io.IOException;

public interface BitOutputStreamable {
    int BITS_PER_BYTE = 8;
    /**
     * Write some bits
     *
     * @param bit     0 or 1 to be written
     * @throws IOException if an I/O error occurs
     */
    void write(int bit) throws IOException;
}
