package org.nirvana.bitIO;

import java.io.EOFException;
import java.io.IOException;

public interface BitInputStreamable {
    int BITS_PER_BYTE = 8;
    /**
     * Reads a byte and return a single bit
     * @return          an integer whose lower bits are read from stream
     * @throws EOFException if no data available from the InputStream
     * @throws IOException  if I/O error occurs
     */
    int read() throws  IOException;


}
