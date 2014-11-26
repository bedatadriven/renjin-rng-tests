package org.renjin.dieharder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Wrapper for the dieharder test suite
 */
public class DieHarder {

  public static class Options {

    private Map<String, String> parameters = new HashMap<String, String>();
    private Set<String> flags = Sets.newHashSet();

    private String STDIN_BINARY_GENERATOR = "200";

    private ExecutorService executorService;
    private Generator generator;
    private String generatorName;


    /**
     * Enables "resolve ambiguity" (RA) mode.  If a test returns "weak",
     * this is an undesired result.  What does that  mean,  after  all?
     * If  you  run  a  long  test series, you will see occasional weak
     * returns  for  a  perfect  generators  because  p  is   uniformly
     * distributed  and will appear in any finite interval from time to
     * time.  Even if a test run returns more than one weak result, you
     * cannot  be  certain that the generator is failing.  RA mode adds
     * psamples (usually in blocks of 100) until the test  result  ends
     * up solidly not weak or proceeds to unambiguous failure.  This is
     * morally equivalent to running the test several times to see if a
     * weak result is reproducible, but eliminates the bias of personal
     * judgement in the process since the default failure threshold  is
     * very small and very unlikely to be reached by random chance even
     * in many runs.
     */
    public Options resolveAmbiguity() {
      parameters.put("-Y", "1");
      setKolmogrovSmirnovPrecision(Precision.LEVEL_2);
      return this;
    }

    public Options multiplySamples(int factor) {
      parameters.put("-m", Integer.toString(factor));
      return this;
    }

    /**
     * Sets precision of Kolmogorov Smirnov test
     */
    public Options setKolmogrovSmirnovPrecision(Precision precision) {
      parameters.put("-k", Integer.toString(precision.ordinal()));
      return this;
    }

    public Options setGenerator(GeneratorType generatorType) {
      generatorName = generatorType.name();
      parameters.put("-g", generatorType.getCode());
      return this;
    }

    public Options setGenerator(String name, Generator generator) {
      parameters.put("-g", STDIN_BINARY_GENERATOR);
      this.generator = generator;
      this.generatorName = name;
      return this;
    }

    public Options setSampleSize(int i) {
      parameters.put("-t", Integer.toString(i));
      return this;
    }

    private List<String> commandLine() {
      List<String> arguments = Lists.newArrayList();
      arguments.add("dieharder");
      arguments.addAll(flags);
      for (Map.Entry<String, String> parameter : parameters.entrySet()) {
        arguments.add(parameter.getKey());
        arguments.add(parameter.getValue());
      }
      return arguments;
    }


    public Report execute() throws InterruptedException, ExecutionException, IOException {
      return DieHarder.execute(this);
    }

  }

  public static Options allTests() {
    Options options = new Options();
    options.flags.add("-a");
    return options;
  }

  public static Options test(org.renjin.dieharder.Test test) {
    Options options = new Options();
    options.parameters.put("-d", Integer.toString(test.getCode()));
    return options;
  }

  public static Report execute(Options options) throws IOException, ExecutionException, InterruptedException {
    Process process = new ProcessBuilder()
        .command(options.commandLine())
        .redirectErrorStream(true)
        .start();

    ExecutorService executor = Executors.newCachedThreadPool();

    try {

      Future<Integer> exitCode = executor.submit(new ProcessMonitor(process));
      Future<Report> report = executor.submit(new ReportReader(options.generatorName, process.getInputStream()));

      if (options.generator != null) {
        executor.submit(new BitStreamGenerator(options.generator, process.getOutputStream()));
      }

      if (exitCode.get() != 0) {
        throw new RuntimeException("dieharder exited with code " + exitCode.get());
      }

      return report.get();

    } finally {
      executor.shutdownNow();
    }
  }

  private static class ProcessMonitor implements Callable<Integer> {
    private final Process process;

    private ProcessMonitor(Process process) {
      this.process = process;
    }

    @Override
    public Integer call() throws Exception {
      return process.waitFor();
    }
  }

  private static class ReportReader implements Callable<Report> {
    private String generatorName;
    private InputStream inputStream;

    private ReportReader(String generatorName, InputStream inputStream) {
      this.generatorName = generatorName;
      this.inputStream = inputStream;
    }

    @Override
    public Report call() throws Exception {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder report = new StringBuilder();
      String line;
      while((line = reader.readLine()) != null) {
        printStatus(line);
        report.append(line).append("\n");
      }
      return new Report(report.toString());
    }

    private void printStatus(String line) {
      if(line.contains("PASSED") || line.contains("WEAK") || line.contains("FAILED")) {
        String[] columns = line.split("\\|");
        String testName = columns[0].trim();
        String result = columns[5].trim();
        String pValue = columns[4].trim();

        System.out.println(generatorName + ": " + testName + " " + result +
          " (p = " + pValue + ")");
      }
    }
  }

}
