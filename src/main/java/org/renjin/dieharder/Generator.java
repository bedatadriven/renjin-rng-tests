package org.renjin.dieharder;

/**
* Random number generator capable of supplying uniformly distributed
* double precision floating point numbers.
*/
public interface Generator {
  double nextDouble();
}
