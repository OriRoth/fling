package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_$;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q1<Tail extends StateStack<?>> extends StateStack<Tail>implements Term_$<Integer> {
  public Q1(final Tail t) {
    super(t);
  }
  @Override public Integer $() {
    return null;
  }
}
