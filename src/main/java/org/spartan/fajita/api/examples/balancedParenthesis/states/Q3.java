package org.spartan.fajita.api.examples.balancedParenthesis.states;

public class Q3<Tail extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, RP extends BaseState<?, ?, ?, ?, ?>>
    extends BaseState<Tail, BaseState.Error, RP, Integer, BaseState.Error> {
  public Q3(final Tail t) {
    super(t);
  }
  @Override public RP rp() {
    return ((RP) t.t.BALANCED().rp());
  }
  @Override public Integer $() {
    return (Integer) t.t.BALANCED().$();
  }
}
