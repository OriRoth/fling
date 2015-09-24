package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.parser.stack.EmptyStack;

public class Q0 extends BaseState<EmptyStack, Q2<Q0, Q1>, BaseState.Error, BaseState.Error, Q1<Q0>> {
  public Q0() {
    super(new EmptyStack());
  }
  // terminals part
  @Override public Q2<Q0, Q1> lp() {
    return new Q2<>(this);
  }
  // nonterminals part
  @Override protected Q1<Q0> BALANCED() {
    return new Q1<>(this);
  }
}
