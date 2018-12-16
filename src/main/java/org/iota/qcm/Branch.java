package org.iota.qcm;

import java.util.LinkedList;
import java.util.List;
import java.util.function.*;

public class Branch {
  int[] inputLength;
  Site[] latches;
  Site[] body;
  Site[] outputs;

  private int subBranchCount;
  private Function<TritVector[], TritVector>[] buildInputbranch;
  private Function<TritVector[], TritVector[]>[] buildInputMerge;

  public Branch(int[] i, Site[] l, Site[] b, Site[] o) {
    inputLength = i;
    latches = l;
    body = b;
    outputs = o;
    prepareInputBuilders();
  }

  public int outputLength() {
    int l = 0;
    for(Site s: outputs) {
      l += s.size();
    }
    return l;
  }

  private int siteLength(int i) {
    if(inputLength.length <= i) {
      i -= inputLength.length;
    } else {
      return inputLength[i];
    }

    if(latches.length <= i) {
      i -= latches.length;
    } else {
      return latches[i].size();
    }


    if(body.length <= i) {
      i -= body.length;
    } else {
      return body[i].size();
    }

    return outputs[i].size();
  }

  private int inputSiteOffset(int i) {
    int j = i, o = 0;
    for(; j-- > 0;) {
      o += inputLength[j];
    }
    return o;
  }

  private void prepareInputBuilders() {
    int i;

    List<Function<TritVector[], TritVector>> buildInputbranchList = new LinkedList<>();
    List<Function<TritVector[], TritVector[]>> buildInputMergeList = new LinkedList<>();

    for(i = 0; i < body.length; i++) {
      addSiteInputBuilder(body[i], buildInputbranchList, buildInputMergeList);
    }
    for(i = 0; i < outputs.length; i++) {
      addSiteInputBuilder(outputs[i], buildInputbranchList, buildInputMergeList);
    }
    for(i = 0; i < latches.length; i++) {
      addSiteInputBuilder(latches[i], buildInputbranchList, buildInputMergeList);
    }

    subBranchCount = buildInputbranchList.size();

    buildInputbranch = buildInputbranchList.toArray(new Function[0]);
    buildInputMerge = buildInputMergeList.toArray(new Function[0]);
  }

  private void addSiteInputBuilder(
      Site site
      , List<Function<TritVector[], TritVector>> buildInputbranchList
      , List<Function<TritVector[], TritVector[]>> buildInputMergeList
  ) {
    switch(site.variant) {
      case SITE_VARIANT_BRANCH:
        buildInputbranchList.add(createBranchInputBuilder(site));
        break;
      case SITE_VARIANT_MERGE:
        buildInputMergeList.add(createMergeInputBuilder(site));
        break;
      default: break;
    }
  }

  private Function<TritVector[], TritVector[]> createMergeInputBuilder(
      Site site
  ) {
    int s = site.inputIndices.length;
    int sl = site.size();
    Function<MergeInput, TritVector[]> f = createMergeInputWriter(site);

    return v -> {
      MergeInput b = new MergeInput();
      b.i = new TritVector[s];
      for(int i = 0; i < s; i++) {
        b.i[i] = new TritVector(sl);
      }
      b.sites = v;
      return f.apply(b);
    };
  }

  private Function<MergeInput, TritVector[]> createMergeInputWriter(
      Site site
  ) {
    int o = 0;
    Function<MergeInput, MergeInput> f = b -> b;
    for(int i : site.inputIndices) {
      f = nextMergeInputWriter(f, i, o);
      o++;
    }
    return finalizeMergeInputWriter(f);
  }

  private Function<MergeInput, MergeInput> nextMergeInputWriter(
      Function<MergeInput, MergeInput> f
      , int i
      , int o
  ) {
    int s;
    if(i < inputLength.length) {
      s = inputSiteOffset(i);
      return f.andThen(b -> {
        b.i[o] = new TritVector(inputLength[i]);
        b.i[o].shift(b.sites[b.sites.length - 1], s);
        return b;
      });
    }
    return f.andThen(b -> {
      b.i[o] = b.sites[i];
      return b;
    });
  }

  private Function<MergeInput, TritVector[]> finalizeMergeInputWriter(
      Function<MergeInput, MergeInput> f
  ) {
    return f.andThen(b -> b.i);
  }

  private Function<TritVector[], TritVector> createBranchInputBuilder(
      Site site
  ) {
    int s = site.size();
    Function<BranchInput, TritVector> f = createBranchInputWriter(site);

    return v -> {
      BranchInput b = new BranchInput();
      b.i = new TritVector(s);
      b.sites = v;
      return f.apply(b);
    };
  }

  private Function<BranchInput, TritVector> createBranchInputWriter(
      Site site
  ) {
    int o = 0;
    Function<BranchInput, BranchInput> f = b -> b;
    for(int i : site.inputIndices) {
      f = nextBranchInputWriter(f, i, o);
      o += siteLength(i);
    }
    return finalizeBranchInputWriter(f);
  }

  private Function<BranchInput, BranchInput> nextBranchInputWriter(
      Function<BranchInput, BranchInput> f
      , int i
      , int o
  ) {
    return f.andThen(b -> {
      b.i.append(b.sites[i], o);
      return b;
    });
  }

  private Function<BranchInput, TritVector> finalizeBranchInputWriter(
      Function<BranchInput, BranchInput> f
  ) {
    return f.andThen(b -> b.i);
  }

  public void compile(BranchInstance b, int maxDepth) {
    int i, j, arn, o;
    int[] k = new int[3];
    BiFunction<TritVector, TritVector, TritVector[]> entry;
    Function<TritVector[], TritVector[]> f;

    if(b.depth > maxDepth) {
      b.f.set((in, out) -> {
        throw new RuntimeException("Exceeded max depth: " + maxDepth);
      });
      return;
    }

    f = v -> v;
    // input, output, body sites, extra output sites
    arn = body.length + outputs.length + 2;

    entry = (in, out) -> {
      TritVector[] tv = new TritVector[arn];
      tv[arn-1] = in;
      tv[arn-2] = out;
      return tv;
    };

    b.memoryLatch = new TritVector[latches.length];
    b.knot = new BranchInstance[subBranchCount];

    for(i = 0; i < subBranchCount; i++) {
      b.knot[i] = new BranchInstance(b.depth + 1);
    }

    for(i = 0; i < b.memoryLatch.length; i++) {
      b.memoryLatch[i] = new TritVector(latches[i].size());
      TritVector.zero(b.memoryLatch[i]);
    }

    for(i = 0; i < body.length; i++) {
      f = compileSite(f, body[i], i, maxDepth, k, b);
    }


    for(i = 0, o = 0, j = latches.length; i < outputs.length; i++, j++) {
      f = compileSite(f, outputs[i], i, maxDepth, k, b);
      f = appendOutput(f, outputs[i], j, o);
      o += outputs[i].size();
    }

    for(i = 0; i < latches.length; i++) {
      f = compileSite(f, latches[i], i, maxDepth, k, b);
    }

    b.f.lazySet(completeBranchInstance(entry, f));

  }

  private Function<TritVector[], TritVector[]> appendOutput(Function<TritVector[], TritVector[]> f, Site output, int i, int o) {
    int j, s;
    if(i < inputLength.length) {
      s = inputSiteOffset(i);
      return f.andThen(v -> {
        TritVector t = new TritVector(inputLength[i]);
        t.shift(v[v.length - 1], s);
        v[v.length - 2].append(t, o);
        return v;
      });
    }
    return f.andThen(v -> {
      v[v.length - 2].append(v[i], o);
      return v;
    });
  }

  private BiConsumer<TritVector, TritVector> completeBranchInstance(
      BiFunction<TritVector, TritVector, TritVector[]> entry
      , Function<TritVector[], TritVector[]> f
  ) {
    return (in, out) -> f.apply(entry.apply(in, out));
  }

  private Function<TritVector[], TritVector[]> compileSite (
      Function<TritVector[] , TritVector[]> f
      , Site site
      , int index
      , int maxDepth
      , int[] indices
      , BranchInstance b
  ) {
    switch (site.variant) {
      case SITE_VARIANT_BRANCH:
        f = compileKnot(f, index, site.size(), maxDepth, b.knot[indices[0]], site, buildInputbranch[indices[0]]);
        indices[0]++;
        break;
      case SITE_VARIANT_MERGE:
        f = compileMerge(f, buildInputMerge[indices[1]++], site, index);
        break;
      case SITE_VARIANT_CONSTANT:
        f = f.andThen(v -> {
          v[index] = site.value;
          return v;
        });
        break;
    }
    return f;
  }

  private Function<TritVector[], TritVector[]> compileMerge (
      Function<TritVector[] , TritVector[]> f
      , Function<TritVector[] , TritVector[]> mb
      , Site site
      , int i
  ) {
    site.length = siteLength(site.inputIndices[0]);
    int size = site.size();
    return f.andThen(v -> {
      v[i] = new TritVector(size);
      merge(mb.apply(v), v[i], size);
      return v;
    });
  }

  private Function<TritVector[], TritVector[]> compileKnot (
      Function<TritVector[] , TritVector[]> f
      , int siteIndex
      , int siteLength
      , int maxDepth
      , BranchInstance knotInstance
      , Site site
      , Function<TritVector[], TritVector> br
  ) {
    knotInstance.f.lazySet((in, out) -> {
      site.branch.compile(knotInstance, maxDepth);
      knotInstance.f.get().accept(in, out);
    });
    return f.andThen(v -> {
      v[siteIndex] = new TritVector(siteLength);
      return v;
    }).andThen(v -> {
      knotInstance.f.get().accept(br.apply(v), v[siteIndex]);
      return v;
    });
  }

  private static void merge(TritVector[] vectors, TritVector out, int length) {
    int i;
    byte b;

    for (TritVector v : vectors
        ) {
      for(i = 0; i < v.value.length; i++) {
        b = out.value[i];
        b ^= v.value[i];
        if(b != 0) {
          if(b == v.value[i]) {
            out.value[i] = v.value[i];
          } else {
            throw new RuntimeException("vector collision detected");
          }
        }
      }
    }
  }

  private class BranchInput {
    TritVector[] sites;
    TritVector i;
  }

  private class MergeInput {
    TritVector[] sites;
    TritVector[] i;
  }
}
