package org.iota.qcm;

public class Site {
  enum SiteVariant {
    SITE_VARIANT_BRANCH,
    SITE_VARIANT_MERGE,
    SITE_VARIANT_CONSTANT
  }

  public SiteVariant variant;
  public Branch branch;
  public TritVector value;
  public int length;
  public int[] inputIndices;

  public int size() {
    int s = 0;

    switch (variant) {
      case SITE_VARIANT_BRANCH:
        for (int l : branch.inputLength) {
          s += l;
        }
        break;
      default:
        s = length;
    }
    return s;
  }
}
