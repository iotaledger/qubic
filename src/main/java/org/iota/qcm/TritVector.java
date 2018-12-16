package org.iota.qcm;

public class TritVector {
  public static final int TRITS_PER_BYTE = 4;
  public byte[] value;

  public TritVector(int size) {
    value = new byte[size / TRITS_PER_BYTE + (size % TRITS_PER_BYTE != 0 ? 1 : 0)];
  }

  public void append(TritVector x, int offset) {
    int startIndex, shift, nshift, i;
    byte c;

    startIndex = offset / TRITS_PER_BYTE;
    shift = offset % TRITS_PER_BYTE;

    if(shift == 0) {
      System.arraycopy(x.value, 0, value, startIndex, x.value.length);
    } else {
      i = 0;
      nshift = TRITS_PER_BYTE - shift;
      for(byte b: x.value) {
        c = b;
        c <<= shift;
        value[i++] |= c;
        c = ~0;
        c <<= shift;
        value[i] = b;
        value[i] >>= nshift;
        value[i] |= ~c;
      }
    }
  }

  public void shift(TritVector x, int offset) {
    int startIndex, shift, nshift, i, j;
    byte b;

    startIndex = offset / TRITS_PER_BYTE;
    shift = (offset % TRITS_PER_BYTE) << 1;

    if(shift == 0) {
      System.arraycopy(x.value, startIndex, value, 0, value.length);
    } else {
      nshift = (TRITS_PER_BYTE << 1) - shift;
      for(j = 0, i = startIndex; j < value.length; j++) {
        value[j] = x.value[i++];
        value[j] >>= nshift;
        b = x.value[i];
        b <<= shift;
        value[j] |= b;
      }
    }
  }

  public static void zero(TritVector t) {
    for(int i = 0; i < t.value.length; i++) {
      t.value[i] |= 0xff;
    }
  }
}
