package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_$;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q9<Tail extends StateStack<BOTTOM>, BOTTOM extends StateStack<?> & NT_START<AFTER_START>, AFTER_START extends StateStack<?>>
    extends StateStack<Tail>implements Term_$<Integer> {
  public Q9(final Tail t) {
    super(t);
  }
  @Override public Integer $() {
    return null;
  }
}
