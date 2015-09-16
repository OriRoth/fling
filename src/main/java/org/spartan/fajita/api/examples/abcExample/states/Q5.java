package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_$;
import org.spartan.fajita.api.parser.stack.RuntimeStateStack;

public class Q5<Tail extends RuntimeStateStack<? extends RuntimeStateStack<START_REDUCTION>>, START_REDUCTION extends RuntimeStateStack<?> & NT_START<AFTER_START>, AFTER_START extends RuntimeStateStack<?> & Term_$<String>>
    extends RuntimeStateStack<Tail>implements Term_$<String> {
  public Q5(final Tail t) {
    super(t);
  }
  @Override public String $() {
    return t.t.t.START().$();
  }
}
