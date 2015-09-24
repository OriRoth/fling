package org.spartan.fajita.api.examples.balancedParenthesis.states;

public class Q5<BOTTOM extends BaseState<? extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, ?, ?, ?, ?>, REDUCE extends BaseState<?, ?, ?, ?, ?>>
    extends BaseState<BOTTOM, BaseState.Error, REDUCE, Integer, BaseState.Error> {
  public Q5(final BOTTOM t) {
    super(t);
  }
  @Override public Integer $() {
    return (Integer) t.t.t.BALANCED().$();
  }
  @SuppressWarnings("unchecked") @Override public REDUCE rp() {
    return (REDUCE) t.t.t.BALANCED().rp();
  }
}
