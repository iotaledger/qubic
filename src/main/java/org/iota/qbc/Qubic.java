package org.iota.qbc;

import org.iota.graph.Graph;

import java.util.Map;

public class Qubic {

  enum Trit {
    POS,
    NEG,
    ZER,
  }
  public static Trit createCommittee(long epochDuration,
                                     long resourceTestDuration,
                                     long weightedStake,
                                     Map<Hash, Long> stakes) {
    return null;
  }

  /*
   */
  public static Trit joinCommittee(Hash committeeId, boolean wait) {
    return null;
  }
  /*
  public static Trit getVertexConsistency(Hash vertex) {
    return NIL;
  }
  */
}
