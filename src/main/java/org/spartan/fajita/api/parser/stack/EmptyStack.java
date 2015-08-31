package org.spartan.fajita.api.parser.stack;

public class EmptyStack implements IStack<Void, EmptyStack> {
  // this class should be subclassed by all the endpoints.
  public Stack<Integer, EmptyStack> push(final Integer i) {
    return new Stack<>(i, this);
  }
  @Override public Void peek() {
    return null;
  }
}
