package automaton;

import automaton.ContextSensitive.End;
import automaton.ecoop.Domain.Stack;
import automaton.ecoop.Domain.Stack.E;
import automaton.ecoop.Domain.Stack.P;

class ContextSensitive {
  public static interface End {
    void $();
  }

  // Type Arguments : <PopLeft , PopRight , PopBoth >
  public static interface C_$_$ {
    C_L_L<C_$_L, C_L_$, C_$_$> a();
  }

  public static interface C_$_L {
    End c();
  }

  public static interface C_L_$ {
  }

  public static interface C_L_L<LPop, RPop, BPop> {
    C_a_a<C_L_a,C_a_L,C_L_L<LPop,RPop,BPop>> a();
  }

  public static interface C_a_a<LPop,RPop,BPop>{
    C_a_a<C_a_a<LPop,>,,C_a_a<LPop,RPop,BPop>> a();
    
    b();
  }

  public static interface C_L_a {
    C_$_a b();
  }

}