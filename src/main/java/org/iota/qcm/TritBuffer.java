package org.iota.qcm;

import org.iota.qbc.TritMath;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static org.iota.qbc.TritMath.TRITS_PER_SHORT;

public class TritBuffer {
  ShortBuffer shortBuffer;
  byte buf;
  int rem;
  short b;
  ByteBuffer byteBuffer = ByteBuffer.allocate(TRITS_PER_SHORT);


  public TritBuffer(short[] shorts, int count) {
    shortBuffer = ShortBuffer.wrap(shorts);
    rem = count;
    buf = 0;//(byte) Math.min(rem, TritMath.TRITS_PER_SHORT);
    //nextTrits();
  }

  public TritBuffer() {}

  public static TritBuffer cloneRange(TritBuffer toClone, int count) {
    TritBuffer tritBuffer = new TritBuffer();
    tritBuffer.shortBuffer = toClone.shortBuffer.duplicate();
    tritBuffer.rem = count;
    tritBuffer.b = toClone.b;
    tritBuffer.buf = toClone.buf;
    toClone.skip(count);
    return tritBuffer;
  }

  public void skip(int count) {
    for(int i = 0; i < count; i++) {
      nextTrit();
    }
  }

  public void checkRemaining() {
    if(--rem == 0) {
      throw new BufferOverflowException();
    }
    if(buf == 0) {
      nextTritt();
    }
  }

  public byte nextTrit() {
    checkRemaining();

    buf--;
    if (b > TritMath.short_radix[buf] / 2) {
      b -= TritMath.short_radix[buf];
      return 1;
    } else if (b < -(TritMath.short_radix[buf] / 2)) {
      b += TritMath.short_radix[buf];
      return -1;
    } else {
      return 0;
    }
  }

  public void fillBuffer(short[] out, int count) {
    int i, j, r;
    short s;

    for(checkRemaining(), i = 0; i < count; checkRemaining()) {
      out[i] = b;
      for(r = buf; r < TritMath.TRITS_PER_SHORT; r++) {
        switch (nextTrit()) {
          case 1: out[i] -= TritMath.short_radix[buf+1];
          break;
          case -1: out[i] += TritMath.short_radix[buf+1];
          break;
        }
      }
    }
  }

  public int nextPositiveInteger() {
    int val = 0;
    int i = 0;
    byte b;
    while((b = nextTrit()) != 0) {
      if(b == 1) {
        val |= 1 << i++;
      }
    }
    return val;
  }

  private void nextTritt() {
    b = shortBuffer.get();
    buf = TritMath.TRITS_PER_SHORT;
  }

  private void nextTrits() {
    short v = shortBuffer.get();
    byteBuffer.clear();

    for(int i = TRITS_PER_SHORT; i-- > 0;) {
      if (v > TritMath.short_radix[i] / 2) {
        v -= TritMath.short_radix[i];
        byteBuffer.put((byte)1);
      } else if (v < -(TritMath.short_radix[i] / 2)) {
        v += TritMath.short_radix[i];
        byteBuffer.put((byte)-1);
      } else {
        byteBuffer.put((byte)0);
      }
    }
  }

  public int nextInteger(int nTrits) {
    return 0;
  }
}
