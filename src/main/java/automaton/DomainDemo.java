package automaton;

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
  
  public static void four_use_cases_of_binary_function_f() {
    γ1 _1 = f.r1().s1();     // ✓
    γ2 _2 = f.r1().s2();     // ✓
    γ2 _3 = f.r2().s1();     // ✓
    f.r2().s2().g(); // ✗ s2() undefined in type Γʹ
  }
  public static void use_cases_stack() {
    P<γ1, P<γ2, P<γ1, P<γ2, B>>>> t;
    B x0 = new B();
    P<γ1,B> x1 = x0.γ1();
    P<γ2,P<γ1,B>> x2 = x1.γ2();
    P<γ1,P<γ1,P<γ2,P<γ1,P<γ1,B>>>>> x3 = ((B) null).γ1().γ1().γ2().γ1().γ1();
    P<γ1,P<γ2,P<γ1,P<γ1,B>>>> x4 = x3.pop();
    P<γ2,P<γ1,P<γ1,B>>> x5 = x4.pop();
    P<γ1,P<γ1,B>> x6 = x5.pop();
    P<γ1,B> x7 = x6.pop();
    B x8 = x7.pop();
    ¤ x9 = x8.pop();
  }
}
