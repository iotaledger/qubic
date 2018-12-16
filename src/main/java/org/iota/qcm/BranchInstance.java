package org.iota.qcm;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class BranchInstance {
  TritVector[] memoryLatch;
  BranchInstance[] knot;
  int depth;
  AtomicReference<BiConsumer<TritVector, TritVector>> f = new AtomicReference<>();

  public BranchInstance(int d) {
    depth = d;
  }
}
