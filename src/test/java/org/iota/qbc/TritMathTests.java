package org.iota.qbc;

import org.junit.Assert;
import org.junit.Test;

public class TritMathTests {
  @Test
  public void testBct() {
    int n = 6;
    short[] v = new short[]{245 * 3, 13};
    byte[] out = new byte[]{0,0};
    byte[] exp = new byte[]{-10,7};
    TritMath.toBct(v, 1, out, 0, n);
    Assert.assertArrayEquals(exp, out);
  }
}
