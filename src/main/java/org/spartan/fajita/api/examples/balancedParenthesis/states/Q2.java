package org.spartan.fajita.api.examples.balancedParenthesis.states;

import org.spartan.fajita.api.parser.stack.EmptyStack;

public class Q2<Stack extends BaseState<?, ?, ?, ?, ?>, AFTER_BALANCED extends BaseState>
    extends BaseState<Stack, Q2<Q2<Stack, ?>, Q4>, Q3<Q2<Stack, ?>, AFTER_BALANCED>, EmptyStack, Q4<Q2<Stack, ?>, AFTER_BALANCED>> {
  public Q2(final Stack t) {
    super(t);
  }
  @Override public Q2<Q2<Stack, ?>, Q4> lp() {
    return new Q2<>(this);
  }
  @Override public Q3<Q2<Stack, ?>, AFTER_BALANCED> rp() {
    return new Q3<>(this);
  }
  @Override protected Q4<Q2<Stack, ?>, AFTER_BALANCED> BALANCED() {
    return new Q4<>(this);
  }
}
