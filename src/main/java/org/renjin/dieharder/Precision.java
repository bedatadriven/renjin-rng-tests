package org.renjin.dieharder;

/**
* Sets the precision level for the K-S test.
*/
public enum Precision {

  /**
   *  fast but slightly sloppy for psamples > 4999 (default).
   */
  LEVEL_0,

  /**
   *  MUCH  slower  but  more  accurate  for  larger  numbers  of
   *  psamples.
   */

  LEVEL_1,
  /**
   * is  slower still, but (we hope) accurate to machine precision
   * for any number of psamples up to some as yet  unknown  numerical
   * upper  limit  (it  has  been  tested out to at least hundreds of
   * thousands).
   */
  LEVEL_2,

  /**
   * kuiper  ks,  fast,  quite  inaccurate  for  small  samples,
   * deprecated.
   */
  LEVEL_3;
}
