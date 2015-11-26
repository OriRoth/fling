package automaton;

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
  
  public static void demonstration_of_binary_function_f(){
    γ1 _1 = f.r1().s1();     // ✓
    γ2 _2 = f.r1().s2();     // ✓
    γ2 _3 = f.r2().s1();     // ✓
    f.r2().s2().g(); // ✗ method s2() is undefined for type Γʹ
  }

}
