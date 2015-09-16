package org.spartan.fajita.api.parser.stack;

public abstract class RuntimeStateStack<Tail extends IStack<?>> implements IStack<Tail> {
  public final Tail t;

  public RuntimeStateStack(final Tail t) {
    this.t = t;
  }
}
