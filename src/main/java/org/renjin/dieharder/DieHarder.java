package org.renjin.dieharder;

import java.io.IOException;
import java.io.OutputStream;

public class DieHarder implements Runnable {

  private Process process;
  private volatile boolean running = true;
  private int exitCode;

  public DieHarder(Process process) {
    this.process = process;
  }

  public static DieHarder start() throws IOException {
    Process process = new ProcessBuilder()
        .command("dieharder",
            "-g", "200",  // Read binary input from stdin
            "-a"          // Run all tests
        )
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .start();

    return new DieHarder(process);
  }

  public boolean isRunning() {
    return running;
  }

  public int getExitCode() {
    return exitCode;
  }

  @Override
  public void run() {
    try {
      int exitCode = process.waitFor();
    } catch (InterruptedException e) {
      exitCode = -1;
    } finally {
      running = false;
    }
  }

  public OutputStream getOutputStream() {
    return process.getOutputStream();
  }
}