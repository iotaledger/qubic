package org.iota.qcm;

import org.iota.qbc.TritMath;

public class Lut implements Branch {
  long value;

  public Lut(TritBuffer buffer) {
    int nCells, nIn, i, j;
    long cell;
    byte s, t;

    t = buffer.nextTrit();
    nCells = buffer.nextPositiveInteger();

    switch (t) {
      case 0:
        nIn = 1;
        break;
      case 1:
        nIn = 2;
        break;
      case -1:
        nIn = 3;
        break;
      default: throw new ArrayIndexOutOfBoundsException();
    }

    value = 0;

    for(i = 0; i < nCells; i++) {
      s = TritMath.tritBct(buffer.nextTrit());
      for(j = 1; j < nIn; j++) {
        s <<= 2;
        s |= TritMath.tritBct(buffer.nextTrit());
      }
      cell = TritMath.tritBct(buffer.nextTrit());
      value |= cell << s;
    }
  }

  @Override
  public int inputLength() {
    return 0;
  }

  @Override
  public void compile(BranchInstance b, int d) {
    b.f.lazySet((in, out) -> out.value[0] |= 0x3 & value >> in.value[0]);
  }
}
