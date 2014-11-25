package org.renjin.dieharder;

import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.math.random.MersenneTwister;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DieHarderTest {

  private static final int BUFFER_SIZE = 1024;
  private static final double INTEGER_RANGE = ((long)Integer.MAX_VALUE)-((long)Integer.MIN_VALUE);

  public interface Generator {
    double nextDouble();
  }

  @Test
  public void apacheCommonsMersenneTwister() throws IOException {
    final org.apache.commons.math.random.MersenneTwister twister = new MersenneTwister();
    test(new Generator() {
      @Override
      public double nextDouble() {
        return twister.nextDouble();
      }
    });
  }


  @Test
  public void gnurMersenneTwisterPort() throws IOException {
    final org.renjin.stats.internals.distributions.MersenneTwister twister = new org.renjin.stats.internals.distributions.MersenneTwister();
    test(new Generator() {
      @Override
      public double nextDouble() {
        return twister.nextDouble();
      }
    });
  }

  private void test(Generator generator) throws IOException {
    DieHarder dieHarder = DieHarder.start();
    OutputStream output = dieHarder.getOutputStream();


    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buffer);

    while(dieHarder.isRunning()) {

      buffer.reset();
      while(buffer.size() < BUFFER_SIZE) {
        int value = (int)((generator.nextDouble() * INTEGER_RANGE) - Integer.MAX_VALUE);
        out.writeInt(value);
      }

      try {
        output.write(buffer.toByteArray());
      } catch(IOException e) {
        break;
      }
    }
    assertThat(dieHarder.getExitCode(), equalTo(0));
  }
}
