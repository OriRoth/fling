package org.spartan.fajita.api.parser.stack;

import org.spartan.fajita.api.parser.State;

public class EmptyStack implements IStack<Void, EmptyStack> {
  // this class should be subclassed by all the endpoints.
  public <T extends State> Stack<T, EmptyStack> push(final T t) {
    return new Stack<>(t, this);
  }
}
