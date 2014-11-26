package org.renjin.dieharder;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertFalse;

public class RenjinTest {

  @Test
  public void goldStandardAes() throws InterruptedException, ExecutionException, IOException {
    Report report = DieHarder.allTests()
        .setGenerator(GeneratorType.AES_OFB)
        .resolveAmbiguity()
        .execute();

    System.out.println(report);
    assertFalse(report.anyFailures());
  }

  @Test
  public void apacheCommonsMersenneTwister() throws Exception {
    final org.apache.commons.math.random.MersenneTwister twister =
        new org.apache.commons.math.random.MersenneTwister();

    test(twister.getClass().getName(), new Generator() {
      @Override
      public double nextDouble() {
        return twister.nextDouble();
      }
    });
  }

  @Test
  public void gnurMersenneTwisterPort() throws Exception {

    final org.renjin.stats.internals.distributions.MersenneTwister twister =
        new org.renjin.stats.internals.distributions.MersenneTwister();

    test(twister.getClass().getName(), new Generator() {
      @Override
      public double nextDouble() {
        return twister.nextDouble();
      }
    });
  }

  private void test(String name, Generator generator) throws Exception {
    Report report = DieHarder
        .allTests()
        .setGenerator(name, generator)
        .resolveAmbiguity()
        .execute();

    System.out.println(report);

    assertFalse(report.anyFailures());
  }
}
