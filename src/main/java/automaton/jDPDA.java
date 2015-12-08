package automaton;

import automaton.A.C.Cγ1;
import automaton.A.C.Cγ2;
import automaton.A.C.E;
import automaton.Domain.JS;
import automaton.Domain.JS.P;
import automaton.Domain.JS.¤;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;

//@formatter:off
class A {
  private static class Lʹ { /* Reject */ } 
  private static class L extends Lʹ { /**/ }

  private interface Pγ2γ2_σ1< 
    Rest extends C<?, ?, ?>, 
    J_γ1 extends C<?, ?, ?>, 
    J_γ2 extends C<?, ?, ?>
   > {
  }

  // Configuration of the automaton
  interface C< // Generic parameters:
    Rest extends C<?, ?, ?>,  // The rest of the stack, for pop operations
    J_γ1 extends C<?, ?, ?>,  // Type of jump(γ1), may be rest, or anything in it. 
    J_γ2 extends C<?, ?, ?>>  // Type of jump(γ2), may be rest, or anything in it. 
  {
    C<?, ?, ?> σ1();          // delta transition on σ1
    C<?, ?, ?> σ2();          // delta transition on σ2
    Lʹ $();                   // delta transition on end of input 

    interface ¤ extends C<¤, ¤, ¤> { // Error configuration.
      @Override public ¤ σ1();
      @Override public ¤ σ2();
    }

    public interface E extends C<¤, ¤, ¤> { /* Empty configuration */ }

    interface Cγ1< // Parameters:
      Rest extends C<?, ?, ?>, 
      J_γ1 extends C<?, ?, ?>, 
      J_γ2 extends C<?, ?, ?>> 
    extends C<Rest,J_γ1, J_γ2>  { 
   default Rest pop() {
     return null; 
   }
   <A extends C<X,Y,Z>, X extends C<?,?,?>, Y, Z> int f()
      @Override Cγ2<Rest, J_γ1, J_γ2> σ1();
      @Override L $();        // Accept
    }

    interface Cγ2<Rest extends C<?, ?, ?>, J_γ1 extends C<?, ?, ?>, J_γ2 extends C<?, ?, ?>> extends C<Rest, J_γ1, J_γ2> {
      C<?, ?, ?> σ1();
      C<?, ?, ?> σ2();
    }
  }

  static Cγ1<E, E, E> build = null;

  static void use_cases() {
    Cγ2<E, E, E> σ1 = A.build.σ1();
  }



    JS.P<
      γ1,                                                      // Top   
      JS.P<γ1,JS.P<γ2,JS.E,¤,JS.E>,JS.P<γ2,JS.E,¤,JS.E>,JS.E>,// Rest 
      JS.P<γ1,JS.P<γ2,JS.E,¤,JS.E>,JS.P<γ2,JS.E,¤,JS.E>,JS.E>,// jump(γ1) 
      JS.E                                                    // jump(γ2)                      
    > _1 = JS.empty.γ2().γ1().γ1();
    JS.E _2 = _1.jump_γ2();
    JS.P<
      γ1,                      // Top
      JS.P<γ2,JS.E,¤,JS.E>,    // Rest
      JS.P<γ2,JS.E,¤,JS.E>     // jump(γ1)
      ,JS.E                    // jump(γ2)
    > _3 = _1.jump_γ1();
  }
}
