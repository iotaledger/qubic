package org.iota.qcm;

import org.iota.qbc.Hash;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class Source {
  enum SourceStatus {
    SOURCE_STATUS_NIL,
    SOURCE_STATUS_IMPORTS,
    SOURCE_STATUS_LUTS,
    SOURCE_STATUS_BRANCHES,
    SOURCE_STATUS_DONE
  }

  SourceStatus status;
  TritBuffer mainBuffer;
  int parseIndex
      , parsedImportLength
      ;
  Import[] imports = null;
  Dataflow[] branches = null;
  Lut[] luts = null;
  TritBuffer[] lutTrits = null;
  TritBuffer[] branchTrits = null;

  public Source(TritBuffer buffer) {
    int version, nLut, nBranch, i;

    version = buffer.nextPositiveInteger();
    if (version != 0) {
      throw new NotImplementedException();
    }
    status = SourceStatus.SOURCE_STATUS_NIL;
    parseIndex = 0;
    parsedImportLength = 0;
  }

  public Branch get(int index, Map<Hash, Source> refMap) {
    int i, j;

    if(status == SourceStatus.SOURCE_STATUS_NIL) {
      j = mainBuffer.nextPositiveInteger();
      imports = new Import[j];
      status = SourceStatus.SOURCE_STATUS_IMPORTS;
    }

    if(status == SourceStatus.SOURCE_STATUS_IMPORTS) {
      for(; parseIndex < imports.length && parsedImportLength < index; parseIndex++) {
        imports[parseIndex] = new Import(mainBuffer);
        parsedImportLength += imports[parseIndex].count;
      }
      if(parseIndex == imports.length) {
        status = SourceStatus.SOURCE_STATUS_LUTS;
      }
    }

    if(status == SourceStatus.SOURCE_STATUS_LUTS) {
      if(lutTrits == null) {
        parseIndex = 0;
        j = mainBuffer.nextPositiveInteger();
        lutTrits = new TritBuffer[j];
      }
      for(; parseIndex < lutTrits.length && (parsedImportLength + parseIndex) < index; parseIndex++) {
        lutTrits[parseIndex] = TritBuffer.cloneRange(mainBuffer, mainBuffer.nextPositiveInteger());
      }
      if(parseIndex == lutTrits.length) {
        status = SourceStatus.SOURCE_STATUS_BRANCHES;
      }
    }

    if(status == SourceStatus.SOURCE_STATUS_BRANCHES) {
      if(branchTrits == null) {
        parseIndex = 0;
        j = mainBuffer.nextPositiveInteger();
        branchTrits = new TritBuffer[j];
      }
      for(; parseIndex < branchTrits.length && (parsedImportLength + parseIndex + lutTrits.length) < index; parseIndex++) {
        branchTrits[parseIndex] = TritBuffer.cloneRange(mainBuffer, mainBuffer.nextPositiveInteger());
      }
      if(parseIndex == branchTrits.length) {
        status = SourceStatus.SOURCE_STATUS_DONE;
      }
    }

    if(parsedImportLength < index) {
      for(i = 0, j = 0;
          j < imports.length
              && i + imports[j].count < index;
          ++j, i += imports[j].count) ;
      return refMap.get(imports[j].source).get(imports[j].indices[index - i], refMap);
    }

    j = index - parsedImportLength;

    if(parsedImportLength + lutTrits.length < index) {
      if(luts == null) {
        luts = new Lut[lutTrits.length];
      }
      luts[j] = new Lut(lutTrits[j]);
      return luts[j];
    }

    j -= lutTrits.length;

    if(j >= branchTrits.length) {
      throw new ArrayIndexOutOfBoundsException("Dataflow index out of bounds");
    }

    if(branches == null) {
      branches = new Dataflow[branchTrits.length];
    }

    j = index;

    if(branches[j] == null) {
      branches[j] = new Dataflow(branchTrits[j], this, refMap);
      if(branches[j] != null) {
        branchTrits[j] = null;
      }
      return branches[j];
    }

    return null;
  }

}
