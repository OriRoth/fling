package automaton;

import automaton.Domain.JS;
import automaton.Domain.JS.E;
import automaton.Domain.JS.P;
import automaton.Domain.JS.¤;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;

//@formatter:off
class JumpStackDemo {
  ;
  interface X extends JS.E{} 
  X x = null;
  void f() {
   P<γ2, JS.P<γ2, E, ¤, E>, E, P<γ2, E, ¤, E>> a = x.γ2().γ2();
 }
  // jump-stack-example.listing
  public static void jump_stack_use_cases(){
    P<
      γ1,                             // Top   
      P<γ1,P<γ2,E,¤,E>,P<γ2,E,¤,E>,E>,// Rest 
      P<γ1,P<γ2,E,¤,E>,P<γ2,E,¤,E>,E>,// jump(γ1) 
      E                               // jump(γ2)                      
    > _1 = JS.empty.γ2().γ1().γ1();
    E _2 = _1.jump_γ2();
    P<
      γ1,             // Top
      P<γ2,E,¤,E>,    // Rest
      P<γ2,E,¤,E>     // jump(γ1)
      ,E              // jump(γ2)
    > _3 = _1.jump_γ1();
  }
}
