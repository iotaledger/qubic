package org.iota.qbc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class TritMath {
  public static int SHORT_RADIX = 19683;
  public static BigInteger BSHORT_RADIX = BigInteger.valueOf(SHORT_RADIX);
  public static BigInteger tbytesToBigInteger(short[] trytes) {
    int i;
    BigInteger v = BigInteger.ZERO;
    for(i = trytes.length; i-- > 0;) {
      v = v.multiply(BSHORT_RADIX).add(BigInteger.valueOf((long)trytes[i]));
    }
    return v;
  }

  public static long tbytesToLong(short[] trytes) {
    int i;
    long v = 0;
    for(i = trytes.length; i-- > 0;) {
      v = (v * SHORT_RADIX) + trytes[i];
    }
    return v;
  }

  public static int longToTBytes(long val, short[] out) {
    int i;
    for(i = 0; i < out.length && val != 0; i++) {
      out[i] = (short) (val % SHORT_RADIX);
      val /= SHORT_RADIX;
    }
    return (int) val;
  }
}
