package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_$;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_c;
import org.spartan.fajita.api.parser.stack.RuntimeStateStack;

public class Q4<Tail extends RuntimeStateStack<START_REDUCTION>, START_REDUCTION extends RuntimeStateStack<?> & NT_START<AFTER_START>, AFTER_START extends RuntimeStateStack<?> & Term_$<String>>
    extends RuntimeStateStack<Tail>implements Term_c<Q5<Q4<Tail, START_REDUCTION, AFTER_START>, START_REDUCTION, AFTER_START>> {
  public Q4(final Tail t) {
    super(t);
  }
  @Override public Q5<Q4<Tail, START_REDUCTION, AFTER_START>, START_REDUCTION, AFTER_START> c() {
    return new Q5<>(this);
  }
}
