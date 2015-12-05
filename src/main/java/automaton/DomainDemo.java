package automaton;

import automaton.Domain.Stack;
import automaton.Domain.Stack.E;
import automaton.Domain.Stack.P;
import automaton.Domain.f;
import automaton.Domain.Γʹ;
import automaton.Domain.Γʹ.Γ;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;

//@Formatter:off
public class DomainDemo {
  class Mammals {
    /* … */ }

  class Heap<M extends Mammals> {
    /* … */}

  class Whales extends Mammals {
    /* … */}

  class School<W extends Whales> extends Heap<W> {
    /* … */}

  // gamma-example.listing
  public static void five_use_cases_of_function_g() {
    γ2 _1 = new γ1().g(); // ✓
    γ1 _2 = new γ2().g(); // ✗ type mismatch
    Γʹ.¤ _3 = new γ2().g(); // ✗ class ¤ is private
    Γʹ _4 = new γ2().g(); // ✓
    _4.g(); // ✗ g() undefined in type Γʹ
  }
  // binary-function-example.listing
  public static void four_use_cases_of_function_f() {
    γ1 _1 = f.r1().s1(); // ✓ ¢$f(r_1,s_1) = \gamma_1$¢
    γ2 _2 = f.r1().s2(); // ✓ ¢$f(r_1,s_2) = \gamma_2$¢
    γ2 _3 = f.r2().s1(); // ✓ ¢$f(r_2,s_1) = \gamma_2$¢
    f.r2().s2().g(); // ✗ method ¢\cc{s2()}¢ undefined in type ¢\cc{$\scriptsize
                     // \Gamma$'}¢
  }
  public static void use_cases_of_stack() {
    // Create a stack a with five items in it:
    P<γ1, P<γ1, P<γ2, P<γ1, P<γ1, E>>>>> _1 = Stack.empty.γ1().γ1().γ2().γ1().γ1();
    P<γ1, P<γ2, P<γ1, P<γ1, E>>>> _2 = _1.pop(); // ✓ Pop one item
    P<γ2, P<γ1, P<γ1, E>>> _3 = _2.pop(); // ✓ Pop another item
    P<γ1, P<γ1, E>> _4 = _3.pop(); // ✓ Pop yet another item
    P<γ1, E> _5 = _4.pop(); // ✓ Pop penultimate item
    γ1 _6 = _5.top(); // ✓ Examine last item
    E _7 = _5.pop(); // ✓ Pop last item
    Stack.¤ _8 = _7.pop(); // ✗ Cannot pop from an empty stack
    Γʹ.¤ _9 = _7.top(); // ✗ empty stack has no top element
  } 
  
  public static class Peep<γ extends Γʹ, S extends Stack<? extends Stack<?>>> {}
  public static void peeping_into_a_stack_use_cases() {
    P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E>>>>> _1 = Stack.empty.γ2().γ1().γ2().γ1().γ2();
    Peep<γ2, P<γ2, P<γ1, P<Γʹ.Γ.γ2, P<γ1, P<γ2, Stack.E>>>>>> _2 = peep(_1);
    E _3 = Stack.empty;
    Peep<?, E> _4 = peep(_3);
  }
  public static Peep<?, E> peep(E _) { return null; } // First overloaded version of ¢\cc{peep()}¢
  public static                                       // Second overloaded version of ¢\cc{peep()}¢
    <Top extends Γ, Rest extends Stack<?>> // Two generic parameters
    Peep<Top, P<Top, Rest>>                // Function return type
  peep(P<Top, Rest> _) { return null; }    // Function parameters and body
  public static 
    <Top1 extends Γ, Top2 extends Γ, Rest extends Stack<?>> // Three generic parameters
    Peel2<Top1, Top2, P<Top2, Rest>>                        // Function return type
    peep2(P<Top1, P<Top2, Rest>> _) { return null; }        // Parameters and body

  //Double peeping
  public static class Peep2<γ_top extends  Γʹ,γ2_second extends  Γʹ, S extends Stack<? extends Stack<?>>> {}
  public static void double_peeping_into_a_stack_use_cases() {
    E _5 = Stack.empty; 
    Peep2<?,?, E>_6 = double_peep(_5);
    P<γ1,E> _3 = Stack.empty.γ1(); 
    Peep2<γ1,?, P<γ1,E>>_4 = double_peep(_3);
    P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E>>>>> _1 = Stack.empty.γ2().γ1().γ2().γ1().γ2(); 
    Peep2<γ2,γ1,P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E>>>>>>  _2 = double_peep_problem_with_type_erasure(_1);
  }
  public static Peep2<?,?, E>  double_peep(E _) { return null; }    // First overloaded version of ¢\cc{peep()}¢
  public static                                                     // Second overloaded version of ¢\cc{peep()}¢
    <Top extends  Γ>                                            // One generic parameter
    Peep2<Top,?, P<Top,E>>                                      // Function return type
    double_peep(P<Top,E> _) { return null; }                    // Function parameters and body
  public static                                                     // Third overloaded version of ¢\cc{peep()}¢
    <Top extends  Γ,Second extends  Γ, Rest extends Stack<?> >  // Three generic parameters
    Peep2<Top,Second, P<Top,P<Second,Rest>>>                  // Function return type
    double_peep_problem_with_type_erasure(P<Top,P<Second,Rest>> _)   { return null; }     // Function parameters and body
  
  
}
