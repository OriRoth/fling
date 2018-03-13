package org.spartan.fajita.revision.motivation;

@SuppressWarnings("all") public class BalancedParenthesisAlgorithmGenerated {
  private interface χ {
  }

  interface α {
  }

  interface π_<l, r, $> {
    l l();
    r r();
    $ $();
  }

  interface π_SrS<l, r, $> {
    π_SrS<χ, π_<π_SrS<l, r, $>, r, $>, χ> l();
    π_<π_SrS<l, r, $>, r, $> r();
    χ $();
  }

  public static π_<π_SrS<χ, χ, α>, χ, α> Λ() {
    return null;
  }
  public static void main(String[] args) {
    α α1 = Λ().l().l().r().l().r().r().$(); // (()()) in L_BP
    α α2 = Λ().l().r().l().r().$(); // ()() in L_BP
    χ χ1 = Λ().l().l().r().r().r(); // (())) not in L_BP
    χ χ2 = Λ().l().l().r().$(); // (() not in L_BP
  }
}
