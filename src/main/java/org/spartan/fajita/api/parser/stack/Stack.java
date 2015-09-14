package org.spartan.fajita.api.parser.stack;

public class Stack<Tail extends IStack<?>> implements IStack<Tail> {
  public final Tail t;

  public Stack(final Tail t) {
    this.t = t;
  }
  public Tail pop() {
    return t;
  }
}
