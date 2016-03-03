package automaton;

import automaton.Domain.Stack;
import automaton.Domain.Stack.E;
import automaton.Domain.Stack.P;

class ContextSensitive{
  
  public static interface C_$_${
    C_L_L a();
  }
  public static interface C_L_L{
    C_a_a<C_L_a> a();
  }
  public static interface C_a_a<Tail>{
    C_a_a<C_a_a<Tail>> a();
    
    b();
  }
  public static interface C_L_a{
   C_$_a b();
  }
  abstract class Γ { 
    public static final class A extends Γ {
    }
    public static final class L extends Γ {
    }
  }
}