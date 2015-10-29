package org.spartan.fajita.api.examples.dependencyCycle;

import org.spartan.fajita.api.parser.stack.EmptyStack;
import org.spartan.fajita.api.parser.stack.IStack;

@SuppressWarnings({ "unchecked", "rawtypes" }) public class States {
  public abstract static class BaseState<Stack extends IStack<?>, d extends BaseState, a extends BaseState, e extends BaseState, A extends BaseState, B extends BaseState>
      implements IStack<Stack> {
    protected final Stack stack;

    BaseState(final Stack stack) {
      this.stack = stack;
    }
    protected d d() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected a a() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected e e() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected A A() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected B B() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected void $() {
      //
    }
  }

  @java.lang.SuppressWarnings({ "serial" }) public static class ParseError extends java.lang.RuntimeException {
    ParseError(final java.lang.String msg) {
      super(msg);
    }
  }

  public static class ErrorState extends BaseState<EmptyStack, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    ErrorState() {
      super(new EmptyStack());
    }
  }

  public static class Q5Q4Q5 extends Q5<Q1<Q0, ?>, Q4<Q2<Q0, ?>, Q5Q4Q5>> {
    Q5Q4Q5(final Q1<Q0, ?> stack) {
      super(stack);
    }
  }

  /* BaseState<Stack,d,a,e,A,B> */
  public static class Q0
      extends BaseState<EmptyStack, ErrorState, Q3<Q0, Q5Q4Q5>, ErrorState, Q1<Q0, Q4<Q2<Q0, ?>, Q5Q4Q5>>, Q2<Q0, Q5Q4Q5>> {
    public Q0() {
      super(new EmptyStack());
    }
    @Override public Q3<Q0, Q5Q4Q5> a() {
      return new Q3<>(this);
    }
    @Override protected Q1<Q0, Q4<Q2<Q0, ?>, Q5Q4Q5>> A() {
      return new Q1<>(this);
    }
    @Override protected Q2<Q0, Q5Q4Q5> B() {
      return new Q2<>(this);
    }
  }

  public static class Q1<Stack extends BaseState<?, ?, ?, ?, ?, ?>, B1 extends BaseState<?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, Q5<Q1<Stack, ?>, B1>, ErrorState, ErrorState> {
    Q1(final Stack stack) {
      super(stack);
    }
    @Override public void $() {
      //
    }
    @Override public Q5<Q1<Stack, ?>, B1> e() {
      return new Q5<>(this);
    }
  }

  public static class Q2<Stack extends BaseState<?, ?, ?, ?, ?, ?>, A1 extends BaseState<?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, Q4<Q2<Stack, ?>, A1>, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q2(final Stack stack) {
      super(stack);
    }
    @Override public Q4<Q2<Stack, ?>, A1> d() {
      return new Q4<>(this);
    }
  }

  public static class Q3<Stack extends BaseState<?, ?, ?, ?, ?, ?>, A1 extends BaseState<?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, A1, ErrorState, ErrorState> {
    Q3(final Stack stack) {
      super(stack);
    }
    @Override public void $() {
      stack.A().$();
    }
    @Override public A1 e() {
      return (A1) stack.A().e();
    }
  }

  public static class Q4<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?>, A2 extends BaseState<?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, A2, ErrorState, ErrorState> {
    Q4(final Stack stack) {
      super(stack);
    }
    @Override public void $() {
      stack.stack.A().$();
    }
    @Override public A2 e() {
      return (A2) stack.stack.A().e();
    }
  }

  public static class Q5<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?>, B2 extends BaseState<?, ?, ?, ?, ?, ?>>
      extends BaseState<Stack, B2, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q5(final Stack stack) {
      super(stack);
    }
    @Override public B2 d() {
      return (B2) stack.stack.B().d();
    }
  }
}
