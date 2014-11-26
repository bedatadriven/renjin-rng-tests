package org.renjin.dieharder;

public class Report {
  private final String text;

  public Report(String text) {
    this.text = text;
  }

  public boolean anyFailures() {
    return text.contains("FAILED");
  }

  public String toString() {
    return text;
  }
}
