package automaton;

import automaton.fjDPDA_2.A.q0lp_Push_q0_f_q2.q2_recursive_q0;
import automaton.fjDPDA_2.A.q2lp_Push_q2_f_q4.q4_recursive_q2;
import automaton.fjDPDA_2.A.q2lp_Push_q2_f_q4.q5_recursive_q2;

/**
 * JLR parser for the grammar : S -> B B -> B lp B rp | ε
 * 
 * that generates the language : L = a^+(b|c)
 */
@SuppressWarnings({ "unused" }) class fjDPDA_2 {
  static class A {
    //@formatter:off
    static class L { /* empty */ }
    public interface E { /* Empty stack configuration */ }
    interface ¤ { /* Error configuration. */ }
    //@formatter:on

    interface Cq0<Rest, JR_lp, JR_rp> extends q0lp_Push_q0_f_q2<Rest, JR_lp, JR_rp, Cq0<Rest, JR_lp, JR_rp>> {
      // inherited lp()
      L $();
    }

    interface Cq2<Rest, JR_lp, JR_rp> extends q2lp_Push_q2_f_q4<Rest, JR_lp, JR_rp, Cq2<Rest, JR_lp, JR_rp>>,q2rp_Push_q2_q5<Rest, JR_lp, JR_rp, Cq2<Rest, JR_lp, JR_rp>> {
      // inherited lp()
      // inherited rp()
    }

    interface Cq4<Rest, JR_lp, JR_rp> extends 
      q4lp_Push_q4_f_q4<Rest, JR_lp, JR_rp, Cq4<Rest, JR_lp, JR_rp>> , 
      q4rp_Push_q4_q7<Rest, JR_lp, JR_rp, Cq4<Rest, JR_lp, JR_rp>>{
      // inherited lp();
      // inherited rp();
    }

    interface Cq5<Rest, JR_lp, JR_rp> {
      JR_lp lp();
      L $();
    }

    interface Cq7<Rest, JR_lp, JR_rp> {
      JR_lp lp();
      JR_rp rp();
    }

    //@formatter:off
    interface q0lp_Push_q0_f_q2<Rest,JR_lp,JR_rp,Me>{
      interface q2_recursive_q0<Rest,JR_lp,JR_rp> extends Cq2<Rest,q2_recursive_q0<Rest,JR_lp,JR_rp>,JR_rp>{
        // auxiliary
      }
      q2_recursive_q0<Rest, JR_lp, JR_rp> lp();
    }
    interface q2rp_Push_q2_q5<Rest,JR_lp,JR_rp,Me>{
      Cq5<Me,JR_lp,JR_rp> rp();
    }
    interface q2lp_Push_q2_f_q4<Rest,JR_lp,JR_rp,Me>{
      interface q4_recursive_q2<Rest,JR_lp,JR_rp> extends Cq4<Cq2<Rest,JR_lp,JR_rp>,q4_recursive_q2<Rest,JR_lp,JR_rp>,q5_recursive_q2<Rest,JR_lp,JR_rp>>{
        //auxiliary
      }
      interface q5_recursive_q2<Rest,JR_lp,JR_rp> extends Cq5<Cq2<Rest,JR_lp,JR_rp>,q4_recursive_q2<Rest,JR_lp,JR_rp>,q5_recursive_q2<Rest,JR_lp,JR_rp>>{
        //auxiliary
      }
      q4_recursive_q2<Me, JR_lp, JR_rp> lp();
    }
    interface q4rp_Push_q4_q7<Rest,JR_lp,JR_rp,Me>{
      Cq7<Me,JR_lp,JR_rp> rp();
    }
    interface q4lp_Push_q4_f_q4<Rest,JR_lp,JR_rp,Me>{
      interface q4_recursive_q4<Rest,JR_lp,JR_rp> extends Cq4<Rest,q4_recursive_q4<Rest,JR_lp,JR_rp>,Cq7<Rest,JR_lp,JR_rp>>{
        //auxiliary
      }
      q4_recursive_q4<Me, JR_lp, JR_rp> lp();
    }
    //@formatter:on

    static Cq0<E, ¤, ¤> build = null;

    static void accepts() {
      A.build.$();
      
      Cq0<E, ¤, ¤> ε0 = A.build; // ε
      q2_recursive_q0<E, ¤, ¤> l0 = ε0.lp(); // (
      Cq5<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤> lr0 = l0.rp(); // ()
      q2_recursive_q0<E, ¤, ¤> lp = lr0.lp();
      A.build.lp().rp().$();
      
      Cq0<E, ¤, ¤> ε1 = A.build; // ε
      q2_recursive_q0<E, ¤, ¤> l1 = ε1.lp(); // (
      q4_recursive_q2<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤> ll1 = l1.lp();
      
      Cq7<Cq4<Cq2<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤>, q4_recursive_q2<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤>, q5_recursive_q2<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤>>, q4_recursive_q2<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤>, q5_recursive_q2<Cq2<E, q2_recursive_q0<E, ¤, ¤>, ¤>, q2_recursive_q0<E, ¤, ¤>, ¤>> llr1 = ll1.rp();
      q5_recursive_q2<Cq2<Cq0<E, ¤, ¤>, q2_recursive_q0<Cq0<E, ¤, ¤>, ¤, ¤, Cq0<E, ¤, ¤>>, ¤>, q2_recursive_q0<Cq0<E, ¤, ¤>, ¤, ¤, Cq0<E, ¤, ¤>>, ¤> llrr1 = llr1.rp();
      q4_recursive_q2<Cq2<Cq0<E, ¤, ¤>, q2_recursive_q0<Cq0<E, ¤, ¤>, ¤, ¤, Cq0<E, ¤, ¤>>, ¤>, q2_recursive_q0<Cq0<E, ¤, ¤>, ¤, ¤, Cq0<E, ¤, ¤>>, ¤> llrrl1 = llrr1.lp();
      
      A.build.lp().lp().lp().lp().rp().lp().rp().rp().rp().rp().$();
    }
    static void rejects() {
      A.build.lp().lp().rp().rp().lp().rp().rp().$();
    }
  }
}