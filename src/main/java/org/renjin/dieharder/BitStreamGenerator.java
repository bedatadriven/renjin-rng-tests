package org.renjin.dieharder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Adapts a random number generator which produces double precision
 * floating numbers to one that generates random bit streams.
 *
 * Since a floating point value actually contains some fairly static
 * bits like the payload and NaN bits so we scale the double output
 * to a 32-bit integer and then write those bits to the stream.
 */
public class BitStreamGenerator implements Runnable {

  private static final int BUFFER_SIZE = 1024;

  private static final double INTEGER_RANGE = ((long) Integer.MAX_VALUE) - ((long) Integer.MIN_VALUE);
  private final ByteBuffer buffer;

  private Generator generator;
  private OutputStream outputStream;

  public BitStreamGenerator(Generator generator, OutputStream outputStream) {
    this.generator = generator;
    this.outputStream = outputStream;
    this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
  }

  @Override
  public void run() {
    try {
      while (true) {
        fillBuffer();
        outputStream.write(buffer.array());
      }
    } catch(IOException e) {
      // Pipe is closed, stop.
    }
  }

  private void fillBuffer() {
    buffer.rewind();
    while(buffer.hasRemaining()) {
      buffer.putInt(nextInt());
    }
  }

  private int nextInt() {
    return (int) ((generator.nextDouble() * INTEGER_RANGE) - Integer.MAX_VALUE);
  }

}