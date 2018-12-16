package org.iota.qbc;

import java.nio.ShortBuffer;
import java.util.*;

import static java.util.Arrays.copyOfRange;

public class Committee {
  public static final int INT_LEN = 27;
  public static final int STAKE_LEN = 81;
  public Hash id;
  public short[] resourceTestDurationTrits= new short[INT_LEN], epochDurationTrits= new short[INT_LEN], resourceStakeTrits = new short[STAKE_LEN];
  public short[] startTime = new short[INT_LEN];
  public Map<Hash, Stake> stakes;

  public void setAttachment(short[] h, short[] attTime) {
    id = new Hash(h);
    System.arraycopy(attTime, 0, startTime, 0, Math.min(startTime.length, attTime.length));
  }


  enum CommitteePhase {
    COMMITTEE_PHASE_TEST,
    COMMITTEE_PHASE_PROC,
  }

  public Committee(short[] h, short[] v) {
    int i = 0;
    id = new Hash(h);
    ShortBuffer s = ShortBuffer.allocate(v.length);
    s.get(epochDurationTrits);
    s.get(resourceTestDurationTrits);
    s.get(startTime);
    stakes = new HashMap<>();
    while(Hash.SIZE_IN_SHORTS + STAKE_LEN <= s.remaining()) {
      stakes.put(
          new Hash(s),
          new Stake(s)
      );
    }
  }

  public Committee(long epochDuration
      , long resourceTestDuration
      , long resourceStake
      , Map<Hash, Stake> stakes) {
    TritMath.longToTBytes(epochDuration, epochDurationTrits);
    TritMath.longToTBytes(resourceTestDuration, resourceTestDurationTrits);
    TritMath.longToTBytes(resourceStake, resourceStakeTrits);
    this.stakes = stakes;
  }

  public long getCommitteeEpochDuration() {
    return TritMath.tbytesToLong(epochDurationTrits);
  }

  public long getCommitteeResourceTestDuration() {
    return TritMath.tbytesToLong(resourceTestDurationTrits);
  }

  public long getCommitteeStartTime() {
    return TritMath.tbytesToLong(startTime);
  }

  public long getCommitteeTestStake() {
    return TritMath.tbytesToLong(resourceStakeTrits);
  }

  public CommitteePhase getCommitteePhase() {
    long st, ed, rtd, p;
    if(id == null) {
      return null;
    } else {
      st = getCommitteeStartTime();
      ed = getCommitteeEpochDuration();
      rtd = getCommitteeResourceTestDuration();
      p = (System.currentTimeMillis() - st) % ed;
      if (p > rtd) {
        return CommitteePhase.COMMITTEE_PHASE_PROC;
      }
      return CommitteePhase.COMMITTEE_PHASE_TEST;
    }
  }

  private int serializedLength() {
    int l = 0;
    l += epochDurationTrits.length;
    l += resourceStakeTrits.length;
    l += resourceTestDurationTrits.length;
    return l;
  }

  public short[] serialize() {
    ShortBuffer s = ShortBuffer.allocate(serializedLength());
    s.put(epochDurationTrits);
    s.put(resourceTestDurationTrits);
    s.put(startTime);
    Iterator<Map.Entry<Hash, Stake>> i = stakes.entrySet().iterator();
    Map.Entry<Hash, Stake> e;
    while(i.hasNext()) {
      e = i.next();
      s.put(e.getKey().value);
      s.put(e.getValue().value);
    }
    return null;
  }
}
