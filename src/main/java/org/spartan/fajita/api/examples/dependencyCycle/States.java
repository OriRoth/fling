package org.spartan.fajita.api.examples.dependencyCycle;

import org.spartan.fajita.api.parser.stack.EmptyStack;
import org.spartan.fajita.api.parser.stack.IStack;

public class States {
  @java.lang.SuppressWarnings({
      "rawtypes" }) public static abstract class BaseState<Stack extends IStack<?>, c extends BaseState, e extends BaseState, b extends BaseState, d extends BaseState, A extends BaseState, B extends BaseState>
          implements IStack<Stack> {
    protected final Stack stack;

    BaseState(final Stack stack) {
      this.stack = stack;
    }
    protected c c() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected e e() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected b b() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected d d() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected A A() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
    protected B B() {
      throw new ParseError("unexpected symbol on state " + getClass().getSimpleName());
    }
  }

  @java.lang.SuppressWarnings({ "serial" }) public static class ParseError extends java.lang.RuntimeException {
    ParseError(final java.lang.String msg) {
      super(msg);
    }
  }

  public static class ErrorState
      extends BaseState<EmptyStack, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState, ErrorState> {
    ErrorState() {
      super(new EmptyStack());
    }
  }

  static class Q0 {
  }

  static class Q1<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?>, B1 extends BaseState<?, ?, ?, ?, ?, ?, ?>> {
  }

  static class Q2<Stack extends BaseState<?, ?, ?, ?, ?, ?, ?>, A1 extends BaseState<?, ?, ?, ?, ?, ?, ?>> {
  }

  static class Q3<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?, ?>, A2 extends BaseState<?, ?, ?, ?, ?, ?, ?>> {
  }

  static class Q4<Stack extends BaseState<? extends BaseState<?, ?, ?, ?, ?, ?, ?>, ?, ?, ?, ?, ?, ?>, B2 extends BaseState<?, ?, ?, ?, ?, ?, ?>> {
  }
}
