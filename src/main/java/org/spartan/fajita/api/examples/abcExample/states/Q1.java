package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.examples.abcExample.bnf.Symbols.*;
import org.spartan.fajita.api.parser.stack.StateStack;

public class Q1<Stack extends StateStack<?> & NT_START<?>> extends 
StateStack<Stack>implements Term_$<String> {
  public Q1(final Stack t) {
    super(t);
  }
  @Override public String $() {
    return "finish";
  }
}
