package automaton;

import automaton.Domain.JS;
import automaton.Domain.JS.E;
import automaton.Domain.JS.P;
import automaton.Domain.JS.¤;
import automaton.Domain.Γʹ;
import automaton.Domain.Γʹ.Γ;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;

public interface jDPDA {
  public interface PJS<T extends Γʹ, R extends JS<?,?,?> > {}
  public static PJS<?, E> pjs(E _) { return null; } 
  public static                                       // Second overloaded version of ¢\cc{pjs()}¢
    <T extends Γ, R extends JS<?,?,?>, J_γ1 extends JS<?,?,?>, J_γ2 extends JS<?,?,?> >
    PJS<T, P<T, R,    J_γ1,    J_γ1>>                // Function return type
  pjs(P<T, R, J_γ1, J_γ2> _) { return null; }    // Function parameters and body
  
  
  
  
  public static void pjsing_into_a_stack_use_cases() {
 P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, P<γ1, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>> _1 = JS.empty.γ2().γ1().γ2().γ1().γ2();
 PJS<γ2, P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>> _2 = pjs(_1);
    E _3 = JS.empty;
    PJS<?, E> _4 = pjs(_3);
    

  }
    

}
