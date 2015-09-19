package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_BALANCED;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_EMPTY;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_NOT_EMPTY;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_lp;
import org.spartan.fajita.api.parser.stack.EmptyStack;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q0 extends StateStack<EmptyStack>implements NT_START<Q1<Q0>>, NT_BALANCED<Q2<Q0, Q1<Q0>>>,
    NT_NOT_EMPTY<Q3<Q0, Q2<Q0, Q1<Q0>>>>, Term_lp<Q4<Q0, Q3<Q0, Q2<Q0, Q1<Q0>>>>>, NT_EMPTY<Q5<Q0, Q2<Q0, Q1<Q0>>>> {
  public Q0() {
    super(new EmptyStack());
  }
  // terminals part
  @Override public Q4<Q0, Q3<Q0, Q2<Q0, Q1<Q0>>>> lp() {
    return new Q4<>(this);
  }
  // nonterminals part
  @Override public Q1<Q0> START() {
    return new Q1<>(this);
  }
  @Override public Q2<Q0, Q1<Q0>> BALANCED() {
    return new Q2<>(this);
  }
  @Override public Q3<Q0, Q2<Q0, Q1<Q0>>> NOT_EMPTY() {
    return new Q3<>(this);
  }
  @Override public Q5<Q0, Q2<Q0, Q1<Q0>>> EMPTY() {
    return new Q5<>(this);
  }
}
