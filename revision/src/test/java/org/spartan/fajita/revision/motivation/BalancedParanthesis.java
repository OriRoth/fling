package org.spartan.fajita.revision.motivation;

/**
 * S ::= l S r S | ε
 */
@SuppressWarnings("unused") public class BalancedParanthesis {
  public static void main(String[] args) {
    l().r().$().ω();
    l().l().r().l().r().r().$().ω();
    l().l().r().l().l().l().r().r().r().r().$().ω();
    // l().l().r().$();
    // l().l().r().r().r();
    // l().l().r().l().l().r().r().r().r();
  }

  public interface $ {
    ω $();
  }

  public interface ω {
    void ω();
  }

  private interface χ {
    //
  }

  public interface Ϟ0<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ2<χ, γ_2_4_6<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ, S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1> l();
    S_$_0 $();
  }

  public interface Ϟ1<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    ω $();
  }

  public interface Ϟ2<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ3<χ, γ_3_5_7<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> l();
    S_r_0 r();
  }

  public interface Ϟ3<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ3<χ, γ_3_5_7<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> l();
    S_r_0 r();
  }

  public interface Ϟ4<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ6<χ, χ, S_$_2, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> r();
  }

  public interface Ϟ5<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ7<χ, S_r_2, χ, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> r();
  }

  public interface Ϟ6<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ2<χ, γ_2_4_6<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ, S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1> l();
    S_$_2 $();
  }

  public interface Ϟ7<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ3<χ, γ_3_5_7<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ, S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1> l();
    S_r_0 r();
  }

  public interface γ_2_4_6<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ2<χ, γ_2_4_6<χ, χ, S_$_0, χ, χ, χ, χ, γ_2_4_6<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ>, χ, χ, χ, χ, χ, χ, χ> l();
    S_$_0 $();
  }

  public interface γ_3_5_7<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2> {
    Ϟ3<χ, γ_3_5_7<χ, S_r_0, χ, χ, χ, χ, χ, γ_3_5_7<S_l_0, S_r_0, S_$_0, S_l_1, S_r_1, S_$_1, S_l_2, S_r_2, S_$_2>, χ>, χ, χ, S_r_0, χ, χ, χ, χ> l();
    S_r_0 r();
  }

  public static Ϟ2<χ, γ_2_4_6<χ, χ, ω, χ, χ, χ, χ, χ, χ>, χ, χ, χ, ω, χ, χ, χ> l() {
    return null;
  }
}
