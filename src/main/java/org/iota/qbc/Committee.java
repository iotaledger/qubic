package org.iota.qbc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.copyOfRange;

public class Committee {
  public static final int INT_LEN = 27;
  public static final int STAKE_LEN = 81;
  public Hash id;
  public short[] resourceTestDurationTrits, epochDurationTrits, resourceStakeTrits;
  public short[] startTime;
  public Map<Hash, short[]> stakes;

  public void setAttachment(short[] h, short[] attTime) {
    id = new Hash(h);
    startTime = Arrays.copyOfRange(attTime, 0, Math.min(INT_LEN, attTime.length));
  }


  enum CommitteePhase {
    COMMITTEE_PHASE_TEST,
    COMMITTEE_PHASE_PROC,
  }

  public Committee(short[] h, short[] v) {
    int i = 0;
    id = new Hash(h);
    epochDurationTrits = copyOfRange(v, i, i+= INT_LEN);
    resourceTestDurationTrits = copyOfRange(v, i, i+= INT_LEN);
    startTime = copyOfRange(v, i, i+= INT_LEN);
    stakes = new HashMap<Hash, short[]>();
    while(i + Hash.SIZE_IN_SHORTS + STAKE_LEN < v.length) {
      stakes.put(
          new Hash(copyOfRange(v, i, i += Hash.SIZE_IN_SHORTS)),
          copyOfRange(v, i, i += STAKE_LEN)
      );
    }
  }

  public Committee(long epochDuration
      , long resourceTestDuration
      , long resourceStake
      , Map<Hash, short[]> stakes) {
    epochDurationTrits = new short[INT_LEN];
    resourceTestDurationTrits = new short[INT_LEN];
    resourceStakeTrits = new short[STAKE_LEN];
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
}
