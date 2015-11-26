package automaton;

import automaton.Domain.Stack;
import automaton.Domain.Stack.B;
import automaton.Domain.Stack.P;
import automaton.Domain.f;
import automaton.Domain.Γʹ;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;

public class DomainDemo {
  
  
  //gamma-example.listing

  public static void five_use_cases_of_function_g() {
    γ2 _1 = new γ1().g();    // ✓
    γ1 _2 = new γ2().g();    // ✗ type mismatch
    Γʹ.¤  _3 = new γ2().g(); // ✗ class ¤ is private
    Γʹ _4 = new γ2().g();    // ✓
    _4.g();              // ✗ g() undefined in type Γʹ
  } 
  
  //binary-function-example.listing
  
  public static void four_use_cases_of_function_f() {
    γ1 _1 = f.r1().s1();     // ✓
    γ2 _2 = f.r1().s2();     // ✓
    γ2 _3 = f.r2().s1();     // ✓
    f.r2().s2().g(); // ✗ s2() undefined in type Γʹ
  }
  
  public static void use_cases_of_stack() {
    // Create a stack a with five items in it:
    P<γ1,P<γ1,P<γ2,P<γ1,P<γ1,B>>>>> _1 = Stack.bottom.γ1().γ1().γ2().γ1().γ1(); 
    P<γ1,P<γ2,P<γ1,P<γ1,B>>>> _2 = _1.pop(); // ✓ Pup one item
    P<γ2,P<γ1,P<γ1,B>>> _3 = _2.pop();       // ✓ Pop another item
    P<γ1,P<γ1,B>> _4 = _3.pop();             // ✓ Pop yet another item
    P<γ1,B> _5 = _4.pop();                   // ✓ Pop penultimate item
    γ1 _6 = _5.top();                        // ✓ Examine last item 
    B _7 = _5.pop();                         // ✓ Pop last item
    ¤ _8 = _7.pop();                         // ✗ Cannot pop from an empty stack 
    ¤ _9 = _8.pop();                         // ✗ empty stack has no top element 
  }
}
