package org.spartan.fajita.revision.motivation;

@SuppressWarnings("all") public class BalancedParenthesisAlgorithmGenerated {
  private interface β {}
  interface τ {}
  interface π_<l, r, $> {
    l l();
    r r();
    $ $();
  }
  interface π_SrS<l, r, $> {
    π_SrS<β, π_<π_SrS<l, r, $>, r, $>, β> l();
    π_<π_SrS<l, r, $>, r, $> r();
    β $();
  }
  public static π_<π_SrS<β, β, τ>, β, τ> Λ() {
    return null;
  }
  public static void main(String[] args) {
    τ τ1 = Λ().l().l().r().l().r().r().$(); // (()()) in L_BP
    τ τ2 = Λ().l().r().l().r().$(); // ()() in L_BP
    β β1 = Λ().l().l().r().r().r(); // (())) not in L_BP
    β β2 = Λ().l().l().r().$(); // (() not in L_BP
  }
}
