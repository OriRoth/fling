package automaton;

import automaton.Domain.Stack;
import automaton.Domain.Stack.E;
import automaton.Domain.Stack.P;

public abstract class DoubleStack<Rest1 extends Stack<?>, Rest2 extends Stack<?>> {
  public abstract Γʹ top1();
  public abstract Γʹ top2();
  public abstract Rest1 pop1();
  public abstract Rest2 pop2();
  public abstract DoubleStack<?> γ1(); // Push type ¢\cc{\scriptsize
                                       // $\Gamma'.\Gamma.\gamma1$}¢
  public abstract DoubleStack<?> γ2(); // Push type ¢\cc{\scriptsize
                                       // $\Gamma'.\Gamma.\gamma2$}¢

  public static class P<Top extends Γ, Rest extends DoubleStack<?>> extends DoubleStack<Rest> {
    // Type of a non-empty stack:
    @Override public Top top() {
      return null;
    }
    @Override public Rest pop1() {
      return null;
    }
    @Override public P<γ1, P<Top, Rest>> γ1() {
      return null;
    }
    @Override public P<γ2, P<Top, Rest>> γ2() {
      return null;
    }
  }

  public static final class E extends DoubleStack<¤> { // Type of an empty stack
    @Override public Γʹ.¤ top() {
      return null;
    }
    @Override public ¤ pop1() {
      return null;
    }
    @Override public P<γ1, E> γ1() {
      return null;
    }
    @Override public P<γ2, E> γ2() {
      return null;
    }
  }

  public static final E empty = null;

  private static final class ¤ extends DoubleStack<¤> { // Type of pop from
                                                        // empty stack
    @Override public Γʹ.¤ top() {
      return null;
    }
    @Override public ¤ pop1() {
      return null;
    }
    @Override public ¤ γ1() {
      return null;
    }
    @Override public ¤ γ2() {
      return null;
    }
  }
}

class Γʹ { 
  private static abstract class ¤ extends Γʹ {
    // Empty private class, cannot be used by clients.
    private ¤() { /* Private constructor hinders extension by clients */ }
  }
  public  static abstract class Γ extends Γʹ { 
    public abstract Γʹ g(); 
    public static final class γ1 extends Γ {
      // Covariant return type in overriding:
      @Override public γ2 g() { return null; } 
    }
    public static final class γ2 extends Γ {
      // Covariant return type in overriding:
      @Override  public Γʹ.¤ g() { return null; }
    }
  }
} 