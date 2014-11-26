package org.renjin.dieharder;

/**
 * Available tests
 */
public enum Test {
  BIRTHDAYS(0),
  DIEHARD_OPERM5(1),
  DIEHARD_32_BINARY_RANK_TEST(2),
  DIEHARD_BITSTREAM_TEST(4),
  DIEHARD_OPSO(5);

  private int code;

  Test(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
