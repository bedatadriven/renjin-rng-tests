package org.renjin.dieharder;

/**
* Built-in DieHarder Generator Type
*/
public enum GeneratorType {

  AES_OFB("205"),

  /**
   * Reads stream of random bits from standard input
   */
  R_MERSENNE_TWISTER("403");

  private String code;

  GeneratorType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
