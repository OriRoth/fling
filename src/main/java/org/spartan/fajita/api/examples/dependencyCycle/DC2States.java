package org.spartan.fajita.api.examples.dependencyCycle;

import org.spartan.fajita.api.parser.stack.EmptyStack;
import org.spartan.fajita.api.parser.stack.IStack;

@SuppressWarnings({ "rawtypes", "unused" }) public class DC2States {
  @java.lang.SuppressWarnings({
      "rawtypes" }) public abstract static class BaseState<Stack extends IStack<?>, a extends BaseState, A extends BaseState>
          implements IStack<Stack> {
    protected final Stack stack;

    BaseState(final Stack stack) {
      this.stack = stack;
    }
    protected a a() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected A A() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    @SuppressWarnings("static-method") protected int $() {
      return 0;
    }
  }

  @java.lang.SuppressWarnings({ "serial" }) public static class ParseError extends java.lang.RuntimeException {
    ParseError(final java.lang.String msg) {
      super(msg);
    }
  }

  public static class ErrorState extends BaseState<EmptyStack, ErrorState, ErrorState> {
    ErrorState() {
      super(new EmptyStack());
    }
  }

  // /* BaseState<Stack,a,A> */
  // public static class Q1Q1<Stack extends BaseState<?, ?, ?>> extends
  // Q1<Stack, Q1Q1<Stack>> {
  // Q1Q1(final Stack stack) {
  // super(stack);
  // }
  // }
  //
  // public static class Q3Q3<Stack extends BaseState<? extends BaseState<?, ?,
  // ?>, ?, ?>> extends Q3<Stack, Q3Q3<Stack>> {
  // Q3Q3(final Stack stack) {
  // super(stack);
  // }
  // }
  //
  // public static class Q0 extends BaseState<EmptyStack, Q2<Q0, Q3Q3<Q1<Q0,
  // ?>>>, Q1Q1<Q0>> {
  // public Q0() {
  // super(new EmptyStack());
  // }
  // @Override protected Q1Q1<Q0> A() {
  // return new Q1Q1<>(this);
  // }
  // @Override public Q2<Q0, Q3Q3<Q1<Q0, ?>>> a() {
  // return new Q2<>(this);
  // }
  // }
  static class Q0 extends BaseState<EmptyStack, ErrorState, ErrorState> {
    public Q0() {
      super(new EmptyStack());
    }
    @Override protected ErrorState A() {
      // TODO Auto-generated method stub
      return super.A();
    }
    @Override public ErrorState a() {
      // TODO Auto-generated method stub
      return super.a();
    }
  }

  //
  // public static class Q1<Stack extends BaseState<?, ?, ?>, A1 extends
  // BaseState<?, ?, ?>>
  // extends BaseState<Stack, Q3<Q1<Stack, ?>, A1>, ErrorState> {
  // Q1(final Stack stack) {
  // super(stack);
  // }
  // @Override public Q3<Q1<Stack, ?>, A1> a() {
  // return new Q3<>(this);
  // }
  // @Override public int $() {
  // return super.$();
  // }
  // }
  static class Q1<Stack extends BaseState<?, ?, ?>, A_1_a extends BaseState<?, ?, ?>> extends BaseState {
    //
  }

  static class Q1ʹ<Stack extends BaseState<?, ?, ?>, A_1_a extends BaseState<?, ?, ?>> extends BaseState {
    //
  }
  //
  // public static class Q2<Stack extends BaseState<?, ?, ?>, A1 extends
  // BaseState<?, ?, ?>> extends BaseState<Stack, A1, ErrorState> {
  // Q2(final Stack stack) {
  // super(stack);
  // }
  // @Override public A1 a() {
  // return (A1) stack.A().a();
  // }
  // @Override public int $() {
  // return super.$();
  // }
  // }

  static class Q2<Stack extends BaseState<?, ?, ?>, A_1_a extends BaseState<?, ?, ?>> extends BaseState {
    //
  }

  static class Q2ʹ<Stack extends BaseState<?, ?, ?>, A_1_a extends BaseState<?, ?, ?>> extends BaseState {
    //
  }
  //
  // public static class Q3<Stack extends BaseState<? extends BaseState<?, ?,
  // ?>, ?, ?>, A2 extends BaseState<?, ?, ?>>
  // extends BaseState<Stack, A2, ErrorState> {
  // Q3(final Stack stack) {
  // super(stack);
  // }
  // @Override public A2 a() {
  // return (A2) stack.stack.A().a();
  // }
  // @Override public int $() {
  // return super.$();
  // }
  // }

  static class Q3<Stack extends BaseState<? extends BaseState<?, ?, ?>, ?, ?>, A_2_a extends BaseState<?, ?, ?>> extends BaseState {
    //
  }

  static class Q3ʹ<Stack extends BaseState<? extends BaseState<?, ?, ?>, ?, ?>, A_2_a extends BaseState<?, ?, ?>>
      extends BaseState {
    //
  }
}
