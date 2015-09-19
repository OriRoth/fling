package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_BALANCED;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_build;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_rp;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q5<BOTTOM extends StateStack<?> & NT_BALANCED<AFTER_BALANCED>, AFTER_BALANCED extends StateStack<?>>
    extends StateStack<BOTTOM>implements Term_rp<AFTER_BALANCED>, Term_build<AFTER_BALANCED> {
  public Q5(final BOTTOM t) {
    super(t);
  }
  private AFTER_BALANCED reduce() {
    return t.BALANCED();
  }
  @Override public AFTER_BALANCED build() {
    return reduce();
  }
  @Override public AFTER_BALANCED rp() {
    return reduce();
  }
}
