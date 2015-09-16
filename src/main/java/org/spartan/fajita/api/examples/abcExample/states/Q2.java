package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_$;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_b;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q2<Tail extends StateStack<?> & NT_START<AFTER_START>, AFTER_START extends StateStack<?> & Term_$<String>>
    extends StateStack<Tail>implements Term_b<Q4<Q2<Tail, AFTER_START>, Tail, AFTER_START>> {
  public Q2(final Tail t) {
    super(t);
  }
  @Override public Q4<Q2<Tail, AFTER_START>, Tail, AFTER_START> b() {
    return new Q4<>(this);
  }
}
