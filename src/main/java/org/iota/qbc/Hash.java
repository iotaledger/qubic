package org.iota.qbc;

import java.nio.ShortBuffer;

import static org.iota.qbc.TritMath.TRITS_PER_SHORT;

public class Hash {
  public static final int SIZE_IN_TRITS = 243;
  public static final int SIZE_IN_SHORTS = SIZE_IN_TRITS / TRITS_PER_SHORT;
  public short[] value = new short[SIZE_IN_SHORTS];

  public Hash(short[] h) {
    System.arraycopy(h, 0, value, 0, Math.min(h.length, SIZE_IN_SHORTS));
  }
  public Hash(ShortBuffer b) {
    b.get(value);
  }
}
