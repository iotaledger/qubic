package org.iota.qcm;

import org.iota.qbc.Hash;

public class Import {
  public int count;
  public int[] indices;
  public Hash source;

  public Import(TritBuffer buffer) {
    int j;
    short[] hash = new short[Hash.SIZE_IN_SHORTS];
    buffer.fillBuffer(hash, Hash.SIZE_IN_TRITS);
    source = new Hash(hash);
    count = buffer.nextPositiveInteger();
    indices = new int[count];
    for(j = 0; j < count; j++) {
      indices[j] = buffer.nextPositiveInteger();
    }
  }
}
