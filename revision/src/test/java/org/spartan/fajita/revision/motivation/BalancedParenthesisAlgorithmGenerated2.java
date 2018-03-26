package org.spartan.fajita.revision.motivation;

@SuppressWarnings("all") public class BalancedParenthesisAlgorithmGenerated2 {
  interface π__lr<l, r> {
    l l();
    r r();
  }

  interface π__l$<l> {
    l l();
    void $();
  }

  interface π_SrS_r<r> {
    π_SrS_r<π__lr<π_SrS_r<r>, r>> l();
    π__lr<π_SrS_r<r>, r> r();
  }

  interface π_SrS_$ {
    π_SrS_r<π__l$<π_SrS_$>> l();
    π__l$<π_SrS_$> r();
  }

  public static class Γ implements π__lr, π__l$, π_SrS_r, π_SrS_$ {
    @Override public Γ l() {
      return this;
    }
    @Override public Γ r() {
      return this;
    }
    @Override public void $() {
    }
  }

  public static π_SrS_$ l() {
    return new Γ();
  }
  public static void main(String[] args) {
    l().l().r().l().r().r().$(); // (()()) in L_BP
    l().r().l().r().$(); // ()() in L_BP
    // l().l().r().r().r(); // (())) not in L_BP
    // l().l().r().$(); // (() not in L_BP
  }
}
