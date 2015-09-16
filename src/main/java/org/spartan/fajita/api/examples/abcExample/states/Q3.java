package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_A;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_b;
import org.spartan.fajita.api.parser.stack.RuntimeStateStack;

public class Q3<A_REDUCTION extends RuntimeStateStack<?> & NT_A<AFTER_A>, AFTER_A extends RuntimeStateStack<?> & Term_b<AFTER_b>, AFTER_b extends RuntimeStateStack<?>>
    extends RuntimeStateStack<A_REDUCTION>implements Term_b<AFTER_b> {
  public Q3(final A_REDUCTION t) {
    super(t);
  }
  @Override public AFTER_b b() {
    return t.A().b();
  }
}
