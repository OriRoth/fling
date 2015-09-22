package org.spartan.fajita.api.examples.abcExample.states;

public class Q1<Tail extends Stack<?>> extends Stack<Tail> {
  public Q1(final Tail t) {
    super(t);
  }
  @SuppressWarnings("static-method") public String $() {
    return "finish";
  }
}
