package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.NT_A;
import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.Term_$;
import org.spartan.fajita.api.parser.stack.RuntimeStateStack;

public class Q1<Tail extends RuntimeStateStack<?> & NT_A<?>> extends RuntimeStateStack<Tail>implements Term_$<String> {
  public Q1(final Tail t) {
    super(t);
  }
  @Override public String $() {
    return "finish";
  }
}
