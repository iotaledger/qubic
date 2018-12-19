package org.iota.qcm;

public interface Branch {
  int inputLength();
  void compile(BranchInstance b, int d);
}
