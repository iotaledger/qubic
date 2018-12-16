package org.iota.qcm;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class BranchTests {
  @Test
  public void testReorderInputs() {
    TritVector in, out, exp;
    BranchInstance bi;
    Branch b;
    Site[] o;
    int[] ins;
    int maxDepth;

    maxDepth = 3;
    ins = new int[]{3, 4};
    o = new Site[2];

    o[0] = new Site();
    o[0].variant = Site.SiteVariant.SITE_VARIANT_MERGE;
    o[0].inputIndices = new int[]{1};
    o[1] = new Site();
    o[1].variant = Site.SiteVariant.SITE_VARIANT_MERGE;
    o[1].inputIndices = new int[]{0};

    b = new Branch(ins, new Site[0], new Site[0], o);
    bi = new BranchInstance(0);

    b.compile(bi, maxDepth);
    in = new TritVector(7);
    out = new TritVector(b.outputLength());
    in.value[0] = 0b1101111;
    in.value[1] = 0b100101;

    bi.f.get().accept(in, out);

    exp = new TritVector(b.outputLength());
    exp.value[0] = -0b0010101;
    exp.value[1] =  0b0101111;

    Assert.assertArrayEquals(exp.value, out.value);
  }

}
