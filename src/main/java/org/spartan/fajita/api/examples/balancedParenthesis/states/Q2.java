package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.balancedParenthesis.bnf.Symbols.Term_build;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q2<BOTTOM extends StateStack<?> & NT_START<AFTER_START>, AFTER_START extends StateStack<?>> extends StateStack<BOTTOM>
    implements Term_build<Q9<Q2<BOTTOM, AFTER_START>, BOTTOM, AFTER_START>> {
  public Q2(final BOTTOM t) {
    super(t);
  }
  @Override public Q9<Q2<BOTTOM, AFTER_START>, BOTTOM, AFTER_START> build() {
    return new Q9<>(this);
  }
}
