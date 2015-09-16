package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_START;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_$;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q5<Tail extends StateStack<? extends StateStack<START_REDUCTION>>, START_REDUCTION extends StateStack<?> & NT_START<AFTER_START>, AFTER_START extends StateStack<?> & Term_$<String>>
    extends StateStack<Tail>implements Term_$<String> {
  public Q5(final Tail t) {
    super(t);
  }
  @Override public String $() {
    return t.t.t.START().$();
  }
}
