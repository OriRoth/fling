package org.spartan.fajita.api.examples.abcExample.states;

import org.spartan.fajita.api.parser.stack.EmptyStack;

public class Q0 extends Stack<EmptyStack> {
  public Q0() {
    super(new EmptyStack());
  }
  public Q3<Q0> a() {
    return new Q3<>(this);
  }
  @Override protected Q2<Q0> read_A() {
    return new Q2<>(this);
  }
  @Override protected Q1<Q0> read_START() {
    return new Q1<>(this);
  }
}
