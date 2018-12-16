package org.iota.qbc;

import java.nio.ShortBuffer;

import static org.iota.qbc.Committee.INT_LEN;

public class Stake {
  public final short[] value = new short[INT_LEN];

  public Stake(ShortBuffer b) {
    b.get(value);
  }
}
