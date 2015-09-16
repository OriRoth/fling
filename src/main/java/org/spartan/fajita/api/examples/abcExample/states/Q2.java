package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_$;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_b;
import org.spartan.fajita.api.parser.stack.RuntimeStateStack;

public class Q2<Tail extends RuntimeStateStack<?> & NT_START<AFTER_START>, AFTER_START extends RuntimeStateStack<?> & Term_$<String>>
    extends RuntimeStateStack<Tail>implements Term_b<Q4<Q2<Tail, AFTER_START>, Tail, AFTER_START>> {
  public Q2(final Tail t) {
    super(t);
  }
  @Override public Q4<Q2<Tail, AFTER_START>, Tail, AFTER_START> b() {
    return new Q4<>(this);
  }
}
