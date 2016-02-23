package org.spartan.fajita.api.examples.automatonCycles.states;

import automaton.seminar.AbstractStack;
import automaton.seminar.EmptyStack;

public class States {
  @java.lang.SuppressWarnings({
      "rawtypes" }) public abstract static class BaseState<Stack extends AbstractStack<?>, a extends BaseState, d extends BaseState, b extends BaseState, D extends BaseState, B extends BaseState, C extends BaseState, A extends BaseState>
          implements IStack<Stack> {
    protected final Stack stack;

    BaseState(final Stack stack) {
      this.stack = stack;
    }
    protected a a() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected d d() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected b b() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected D D() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected B B() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected C C() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected A A() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected void $() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
  }

  @java.lang.SuppressWarnings({ "serial" }) public static class ParseError extends java.lang.RuntimeException {
    ParseError(final java.lang.String msg) {
      super(msg);
    }
  }

  public static class ErrorState
      extends BaseState<EmptyStack, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    ErrorState() {
      super(new EmptyStack());
    }
  }

  public static class Q0 extends
      BaseState<EmptyStack, Q2<Q0, Q6<Q4<Q0, ?>, Q7<Q3<Q0>>>>, ErrorState, ErrorState, Q1<Q0>, ErrorState, Q3<Q0>, Q4<Q0, Q7<Q3<Q0>>>> {
    public Q0() {
      super(new EmptyStack());
    }
    @Override public Q2<Q0, Q6<Q4<Q0, ?>, Q7<Q3<Q0>>>> a() {
      return new Q2<>(this);
    }
    @Override protected Q4<Q0, Q7<Q3<Q0>>> A() {
      return new Q4<>(this);
    }
    @Override protected Q3<Q0> C() {
      return new Q3<>(this);
    }
    @Override protected Q1<Q0> D() {
      return new Q1<>(this);
    }
  }

  public static class Q1<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q1(Stack stack) {
      super(stack);
    }
    @Override public void $() {
      return;
    }
  }

  public static class Q2<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>, A_1_b extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>> extends
      BaseState<Stack, Q2<Q2<Stack, ?>, A_1_b>, ErrorState, A_1_b, ErrorState, ErrorState, ErrorState, Q8<Q2<Stack, ?>, A_1_b>> {
    Q2(Stack stack) {
      super(stack);
    }
    @Override    public Q2<Q2<Stack, ?>, A_1_b> a() {
      return new Q2<>(this);
    }
    @Override protected Q8<Q2<Stack, ?>, A_1_b> A() {
      return new Q8<>(this);
    }
    @SuppressWarnings("unchecked") @Override public A_1_b b() {
      return (A_1_b) stack.A().b();
    }
  }

  public static class Q3<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, Q7<Q3<Stack>>, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q3(Stack stack) {
      super(stack);
    }
    @Override public Q7<Q3<Stack>> d() {
      return new Q7<>(this);
    }
  }

  public static class Q4<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>, C_1_d extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>> extends
      BaseState<Stack, ErrorState, ErrorState, Q6<Q4<Stack, ?>, C_1_d>, ErrorState, Q5<Q4<Stack, ?>, C_1_d>, ErrorState, ErrorState> {
    Q4(Stack stack) {
      super(stack);
    }
    @Override public Q6<Q4<Stack, ?>, C_1_d> b() {
      return new Q6<>(this);
    }
    @Override protected Q5<Q4<Stack, ?>, C_1_d> B() {
      return new Q5<>(this);
    }
  }

  public static class Q5<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?, ?, ?>, C_2_d extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, C_2_d, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q5(Stack stack) {
      super(stack);
    }
    @SuppressWarnings("unchecked") @Override public C_2_d d() {
      return (C_2_d) stack.stack.d();
    }
  }

  public static class Q6<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>, B_1_d extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, B_1_d, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q6(Stack stack) {
      super(stack);
    }
    @SuppressWarnings("unchecked") @Override public B_1_d d() {
      return (B_1_d) stack.d();
    }
  }

  public static class Q7<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q7(Stack stack) {
      super(stack);
    }
    @Override public void $() {
      stack.stack.$();
    }
  }

  public static class Q8<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?, ?, ?>, A_2_b extends BaseState<?, ?, ?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, A_2_b, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q8(Stack stack) {
      super(stack);
    }
    @SuppressWarnings("unchecked") @Override public A_2_b b() {
      return (A_2_b) stack.stack.A().b();
    }
  }
}
