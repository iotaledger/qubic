package org.iota.qbc;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class CommitteeTest {
  @Test
  public void testCommitteeCreate() {
    Committee c;
    HashMap<Hash, Stake> stakes;
    stakes = new HashMap<>();
    long epoch = 500, resource = 32, stake = 483;
    c = new Committee(epoch, resource, stake, stakes);
    Assert.assertEquals(epoch, c.getCommitteeEpochDuration());
    Assert.assertEquals(resource, c.getCommitteeResourceTestDuration());
    Assert.assertEquals(stake, c.getCommitteeTestStake());
    Assert.assertEquals(null, c.getCommitteePhase());
  }

  @Test
  public void testCommitteeAttach() {
    Committee c;
    HashMap<Hash, Stake> stakes;
    stakes = new HashMap<>();
    long epoch = 500, resource = 32, stake = 483;
    long longID = 1238796132;
    long attachedTime = System.currentTimeMillis();
    short[] id, attTime;
    id = new short[Hash.SIZE_IN_SHORTS];
    attTime = new short[Committee.INT_LEN];
    c = new Committee(epoch, resource, stake, stakes);
    TritMath.longToTBytes(longID, id);
    TritMath.longToTBytes(attachedTime, attTime);
    c.setAttachment(id, attTime);
    Assert.assertEquals(Committee.CommitteePhase.COMMITTEE_PHASE_PROC, c.getCommitteePhase());
  }

}
