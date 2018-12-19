package org.iota.qbc;

import org.iota.qcm.TritVector;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class TritMath {
  public static final int TRITS_PER_SHORT = 9;
  public static int RADIX = 3;
  public static int SHORT_RADIX = 19683;
  public static BigInteger BSHORT_RADIX = BigInteger.valueOf(SHORT_RADIX);

  public static short[] short_radix = new short[] {1,3,9,27,81,243,729,2187,6561};

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

  public static int shortBct(short v) {
    int buf = 0;
    for(int i = TRITS_PER_SHORT; i-- > 0;) {
      buf <<=2;
      buf |= 3;
      if (v > short_radix[i] / 2) {
        v -= short_radix[i];
        buf ^= 2;
      } else if (v < -(short_radix[i] / 2)) {
        v += short_radix[i];
        buf ^= 1;
      }
    }
    return buf;
  }

  public static byte tritBct(byte trit) {
    switch(trit) {
      case 1: return 1;
      case 0: return 3;
      case -1: return 2;
      default: return 0;
    }
  }

  public static void toBct(short[] in, int inOffset, byte[] out, int offset, int length) {
    int outIndex, outOffset, inIndex, count, lastFullByteCount, rem, buf;
    byte mask;

    if(in.length == 0 || out.length == 0 || length == 0) {
      return;
    }

    lastFullByteCount = (((offset + length) / TritVector.TRITS_PER_BYTE) * TritVector.TRITS_PER_BYTE) - offset;

    inIndex = inOffset / TRITS_PER_SHORT;
    buf = shortBct(in[inIndex++]) >> ((inOffset % TRITS_PER_SHORT) << 1);
    rem = TRITS_PER_SHORT;

    outIndex = offset / TritVector.TRITS_PER_BYTE;
    outOffset = offset % TritVector.TRITS_PER_BYTE;
    out[outIndex++] |= buf << (outOffset << 1);
    count = TritVector.TRITS_PER_BYTE - outOffset;
    buf >>= (count << 1);

    for( ; rem - count < lastFullByteCount || rem < length - count; ) {
      if(inIndex < in.length && rem < TritVector.TRITS_PER_BYTE) {
        buf |= shortBct(in[inIndex++]) << (rem << 1);
        rem += TRITS_PER_SHORT;
      } else if (count < lastFullByteCount){
        out[outIndex++] |= buf;
        count += TritVector.TRITS_PER_BYTE;
        rem -= TritVector.TRITS_PER_BYTE;
        buf >>= (TritVector.TRITS_PER_BYTE << 1);
      }
    }

    out[outIndex] |= ~(0xff << ((length - lastFullByteCount) << 1)) & buf;
  }

  public static byte bctValue(int buf) {
    switch(3 & buf) {
      case 3: return 0;
      case 2: return -1;
      case 1: return 1;
      default: return 2;
    }
  }
}
