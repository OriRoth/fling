package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.parser.stack.IStack;
import org.spartan.fajita.api.parser.stack.StateStack;

public abstract class Stack<Tail extends IStack<?>> implements IStack<Tail> {
  public final Tail t;

  public Stack(final Tail t) {
    this.t = t;
  }
  @SuppressWarnings("static-method") protected <S extends StateStack<?>> S read_A() {
    throw new IllegalArgumentException("illegal nonterminal invokation");
  }
  @SuppressWarnings("static-method") protected <S extends StateStack<?>> S read_START() {
    throw new IllegalArgumentException("illegal nonterminal invokation");
  }
}
