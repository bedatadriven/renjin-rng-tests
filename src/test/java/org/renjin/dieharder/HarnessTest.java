package org.renjin.dieharder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

public class HarnessTest {

  @org.junit.Test
  public void test() throws InterruptedException, ExecutionException, IOException {

    Generator nullGenerator = new Generator() {
      @Override
      public double nextDouble() {
        return 1d/3d;
      }
    };

    Report report = DieHarder.test(Test.BIRTHDAYS)
        .setGenerator("Null Generator", nullGenerator)
        .setSampleSize(1000)
        .execute();

    System.out.println(report.toString());

    assertTrue(report.anyFailures());

  }

}
