package org.spartan.fajita.api.examples.balancedParenthesis.states;

import automaton.seminar.AbstractStack;
import automaton.seminar.EmptyStack;

@java.lang.SuppressWarnings({ "rawtypes", "unused" }) class States {
  public abstract static class BaseState<Stack extends AbstractStack<?>, lp extends BaseState, build extends BaseState, rp extends BaseState, BALANCED extends BaseState>
      implements IStack<Stack> {
    protected final Stack stack;

    BaseState(final Stack stack) {
      this.stack = stack;
    }
    protected lp lp() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected build build() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected rp rp() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected BALANCED BALANCED() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
  }

  @java.lang.SuppressWarnings({ "serial" }) public static class ParseError extends java.lang.RuntimeException {
    ParseError(final java.lang.String msg) {
      super(msg);
    }
  }

  public static class ErrorState extends BaseState<EmptyStack, ErrorState, ErrorState, ErrorState, ErrorState> {
    ErrorState() {
      super(new EmptyStack());
    }
  }

  static class Q0 extends BaseState<EmptyStack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q0() {
      super(new EmptyStack());
    }
  }

  static class Q1<Stack extends BaseState<?, ?, ?, ?, ?>> extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q1(final Stack stack) {
      super(stack);
    }
  }

  static class Q1ʹ<Stack extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q1ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q2<Stack extends BaseState<?, ?, ?, ?, ?>> extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q2(final Stack stack) {
      super(stack);
    }
  }

  static class Q2ʹ<Stack extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q2ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q3<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q3(final Stack stack) {
      super(stack);
    }
  }

  static class Q3ʹ<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q3ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q4<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q4(final Stack stack) {
      super(stack);
    }
  }

  static class Q4ʹ<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q4ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q5<Stack extends BaseState<?, ?, ?, ?, ?>, BALANCED_1_rp extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q5(final Stack stack) {
      super(stack);
    }
  }

  static class Q5ʹ<Stack extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q5ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q6<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, BALANCED_2_rp extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q6(final Stack stack) {
      super(stack);
    }
  }

  static class Q6ʹ<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q6ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q7<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, BALANCED_2_rp extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q7(final Stack stack) {
      super(stack);
    }
  }

  static class Q7ʹ<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q7ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q8<Stack extends BaseState<? extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, ?, ?, ?, ?>, BALANCED_3_rp extends BaseState<?, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q8(final Stack stack) {
      super(stack);
    }
  }

  static class Q8ʹ<Stack extends BaseState<? extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q8ʹ(final Stack stack) {
      super(stack);
    }
  }

  static class Q9<Stack extends BaseState<? extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q9(final Stack stack) {
      super(stack);
    }
  }

  static class Q9ʹ<Stack extends BaseState<? extends BaseState<? extends BaseState<?, ?, ?, ?, ?>, ?, ?, ?, ?>, ?, ?, ?, ?>>
      extends BaseState<Stack, ErrorState, ErrorState, ErrorState, ErrorState> {
    Q9ʹ(final Stack stack) {
      super(stack);
    }
  }
}
