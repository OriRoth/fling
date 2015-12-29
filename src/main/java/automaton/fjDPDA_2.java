package automaton;

import automaton.fjDPDA_2.A.Cq0;
import automaton.fjDPDA_2.A.E;
import automaton.fjDPDA_2.A.¤;
import automaton.fjDPDA_2.A.q0lp_Push_q0_f_q2.q2_recursive_q0;
import automaton.fjDPDA_2.A.q2lp_Push_q2_f_q4.q4_recursive_q2;
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

    interface Cq0<Rest, JR_lp, JR_rp> extends 
      q0lp_Push_q0_f_q2<Rest, JR_lp, JR_rp, Cq0<Rest, JR_lp, JR_rp>> {
      // inherited lp()
      L $();
    }

    interface Cq2<Rest, JR_lp, JR_rp> extends 
      q2lp_Push_q2_f_q4<Rest, JR_lp, JR_rp, Cq2<Rest, JR_lp, JR_rp>>,
      q2rp_Push_q2_q5<Rest, JR_lp, JR_rp, Cq2<Rest, JR_lp, JR_rp>> {
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
      interface q2_recursive_q0<Rest,JR_rp> extends Cq2<Rest,q2_recursive_q0<Rest,JR_rp>,JR_rp>{
        // auxiliary
      }
      q2_recursive_q0<Me, JR_rp> lp();
    }
    interface q2rp_Push_q2_q5<Rest,JR_lp,JR_rp,Me>{
      Cq5<Me,JR_lp,JR_rp> rp();
    }
    interface q2lp_Push_q2_f_q4<Rest,JR_lp,JR_rp,Me>{
      interface q4_recursive_q2<Rest,JR_lp,JR_rp> extends Cq4<Rest,q4_recursive_q2<Rest,JR_lp,JR_rp>,Cq5<Rest,JR_lp,JR_rp>>{
        //auxiliary
      }
      q4_recursive_q2<Me,JR_lp,JR_rp> lp();
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
      
      //        (    )
      A.build.lp().rp().$();
      
      //        (    (    (    (    )    (    )    )    )    )
      A.build.lp().lp().lp().lp().rp().lp().rp().rp().rp().rp().$();
    }
    static void rejects() {
      A.build.rp();
      A.build.lp().$();
      A.build.lp().rp().rp();
      
    }
  }
}
