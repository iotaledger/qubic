package org.iota.qcm;

import org.iota.qbc.Hash;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class Site {
  enum SiteVariant {
    SITE_VARIANT_KNOT,
    SITE_VARIANT_MERGE,
    // try to avoid SITE_VARIANT_CONSTANT
  }

  public SiteVariant variant;
  public Branch branch = null;
  public int[] inputIndices;

  public Site() { }

  public Site(TritBuffer buffer, int nTritsPerIndex, Source source, Map<Hash, Source> refmap) {
    switch (buffer.nextTrit()) {
      case 1:
        variant = SiteVariant.SITE_VARIANT_MERGE;
        break;
      case -1:
        variant = SiteVariant.SITE_VARIANT_KNOT;
        break;
      default:
        throw new IndexOutOfBoundsException("Invalid Site Type");
    }

    inputIndices = new int[buffer.nextPositiveInteger()];
    for(int i = 0; i < inputIndices.length; i++) {
      inputIndices[i] = buffer.nextInteger(nTritsPerIndex);
    }

    if(variant == SiteVariant.SITE_VARIANT_KNOT) {
      branch = source.get(buffer.nextPositiveInteger(), refmap);
    }
  }

  public int size() {
    int s = 0;

    switch (variant) {
      case SITE_VARIANT_KNOT:
        s = branch.inputLength();
        break;
      default:
        throw new NotImplementedException();
    }
    return s;
  }
}
