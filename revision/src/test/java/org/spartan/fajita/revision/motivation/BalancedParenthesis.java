package org.spartan.fajita.revision.motivation;

/**
 * Σ = {l, r}, BP = {w | w ∈ Σ* ∧ w is balanced}
 */
public class BalancedParenthesis {
  public static class ΣStar {
    public ΣStar l() {
      return this;
    }
    public ΣStar r() {
      return this;
    }
  }

  public interface BP1 {
    BP2<BP1> l();
    void $();
  }

  public interface BP2<ϱ> {
    BP2<BP2<ϱ>> l();
    ϱ r();
  }

  public static void main(String[] args) {
    new ΣStar().l().r().r().l();
    ((BP1) null) //
        .l() //
        /**/.l() //
        /**/.r() //
        /**/.l() //
        /**//**/.l() //
        /**//**/.r() //
        /**/.r() //
        .r() //
        .$();
  }
}
