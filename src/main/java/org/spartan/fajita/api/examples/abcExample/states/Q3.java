package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.parser.stack.StateStack;

public class Q3<Tail extends StateStack<?>> extends StateStack<Tail> {
  public Q3(final Tail t) {
    super(t);
  }
  @Override protected <S extends StateStack<?>> S readTerminal() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override protected <S extends StateStack<?>> S readNonTerminal() {
    // TODO Auto-generated method stub
    return null;
  }
}
