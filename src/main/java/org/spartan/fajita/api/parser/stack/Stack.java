package org.spartan.fajita.api.parser.stack;

import org.spartan.fajita.api.parser.State;

@SuppressWarnings("rawtypes") public class Stack<Head, Tail extends IStack> implements IStack<Head, Tail> {
  public final Head h;
  public final Tail t;

  public Stack(final Head h, final Tail t) {
    this.h = h;
    this.t = t;
  }
  protected Stack<State, Stack<Head, Tail>> push(final State s) {
    return new Stack<>(s, this);
  }
  protected Tail pop() {
    return t;
  }
}
