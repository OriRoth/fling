package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_NOT_EMPTY;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_build;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_rp;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q8<Tail extends StateStack<? extends StateStack<? extends StateStack<BOTTOM>>>, BOTTOM extends StateStack<?> & NT_NOT_EMPTY<AFTER_NOT_EMPTY>, AFTER_NOT_EMPTY extends StateStack<?>>
    extends StateStack<Tail>implements Term_rp<AFTER_NOT_EMPTY>, Term_build<AFTER_NOT_EMPTY> {
  public Q8(final Tail t) {
    super(t);
  }
  private AFTER_NOT_EMPTY reduce() {
    return t.t.t.t.NOT_EMPTY();
  }
  @Override public AFTER_NOT_EMPTY build() {
    return reduce();
  }
  @Override public AFTER_NOT_EMPTY rp() {
    return reduce();
  }
}
