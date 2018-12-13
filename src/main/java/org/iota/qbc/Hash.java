package org.iota.qbc;

public class Hash {
  public static final int SIZE_IN_TRITS = 243;
  public static final int TRITS_PER_SHORT = 9;
  public static final int SIZE_IN_SHORTS = SIZE_IN_TRITS / TRITS_PER_SHORT;
  public short[] value;

  public Hash(short[] h) {
    value = new short[SIZE_IN_SHORTS];
    System.arraycopy(h, 0, value, 0, Math.min(h.length, SIZE_IN_SHORTS));
  }
}
