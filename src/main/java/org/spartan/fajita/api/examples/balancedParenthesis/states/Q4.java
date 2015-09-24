package org.spartan.fajita.api.examples.balancedParenthesis.states;

public class Q4<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, BALANCED extends BaseState<?, ?, ?, ?, ?>>
    extends BaseState<Stack, BaseState.Error, Q5<Q4<Stack, ?>, BALANCED>, BaseState.Error, BaseState.Error> {
  public Q4(final Stack bottom) {
    super(bottom);
  }
  @Override public Q5<Q4<Stack, ?>, BALANCED> rp() {
    return new Q5<>(this);
  }
}
