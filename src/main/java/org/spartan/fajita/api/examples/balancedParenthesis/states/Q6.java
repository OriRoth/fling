package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_NOT_EMPTY;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_rp;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q6<Tail extends StateStack<BOTTOM>, BOTTOM extends StateStack<?> & NT_NOT_EMPTY<AFTER_NOT_EMPTY>, AFTER_NOT_EMPTY extends StateStack<?>>
    extends StateStack<Tail>implements Term_rp<Q7<Q6<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY>> {
  public Q6(final Tail t) {
    super(t);
  }
  @Override public Q7<Q6<Tail, BOTTOM, AFTER_NOT_EMPTY>, BOTTOM, AFTER_NOT_EMPTY> rp() {
    return new Q7<>(this);
  }
}
