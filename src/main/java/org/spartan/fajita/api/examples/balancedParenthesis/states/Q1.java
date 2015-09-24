package org.spartan.fajita.api.examples.balancedParenthesis.states;

public class Q1<Stack extends BaseState<?, ?, ?, ?, ?>>
    extends BaseState<Stack, BaseState.Error, BaseState.Error, Integer, BaseState.Error> {
  public Q1(final Stack t) {
    super(t);
  }
  @Override public Integer $() {
    return null;
  }
}
