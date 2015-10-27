package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_A;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_a;
import org.spartan.fajita.api.parser.stack.EmptyStack;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q0 extends StateStack<EmptyStack>
    implements NT_A<Q2<Q0, Q1<Q0>>>, NT_START<Q1<Q0>>, Term_a<Q3<Q0, Q2<Q0, Q1<Q0>>, Q4<Q2<Q0, Q1<Q0>>, Q0, Q1<Q0>>>> {
  public Q0() {
    super(new EmptyStack());
  }
  
  // terminal zone
  @Override public Q3<Q0, Q2<Q0, Q1<Q0>>, Q4<Q2<Q0, Q1<Q0>>, Q0, Q1<Q0>>> a() {
    return new Q3<>(this);
  }
  
  //nonterminal zone
  @Override public Q1<Q0> START() {
    return new Q1<>(this);
  }
  @Override public Q2<Q0, Q1<Q0>> A() {
    return new Q2<>(this);
  }
}
