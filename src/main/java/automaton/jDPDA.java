package automaton;

import automaton.A.JS.E;
import automaton.A.JS.P;
import automaton.A.JS.¤;
import automaton.A.Γʹ.Γ;
import automaton.A.Γʹ.Γ.γ1;
import automaton.A.Γʹ.Γ.γ2;

class A {

  interface Γʹ {
    // @Formatter:off
    interface Γ extends Γʹ {
      interface γ1 extends Γ {
      }

      interface γ2 extends Γ {
      }
    }

    interface ¤ extends Γʹ {
    }
    // @Formatter:on
  }

  private interface Pʹ<Top extends Γ, Rest extends JS<?, ?, ?>, J_γ1 extends JS<?, ?, ?>, J_γ2 extends JS<?, ?, ?>, Me extends JS<?, ?, ?>>
      extends JS<Rest, J_γ1, J_γ2> {
    @Override public Top top();
    @Override P<γ1, Me, Me, J_γ2> γ1();
    @Override P<γ2, Me, J_γ2, Me> γ2();
  }

  public interface JS<Rest extends JS<?, ?, ?>, J_γ1 extends JS<?, ?, ?>, J_γ2 extends JS<?, ?, ?>> {
    Γʹ top();
    Rest pop();
    JS<?, ?, ?> γ1();
    JS<?, ?, ?> γ2();
    J_γ1 jump_γ1();
    J_γ2 jump_γ2();

    interface ¤ extends JS<¤, ¤, ¤> {
      @Override public Γʹ.¤ top();
      @Override public ¤ pop();
      @Override public ¤ γ1();
      @Override public ¤ γ2();
    }

    public interface E extends JS<¤, ¤, ¤> {
      @Override public Γʹ.¤ top();
      @Override public ¤ pop();
      @Override public P<γ1, E, E, ¤> γ1();
      @Override public P<γ2, E, ¤, E> γ2();
    }

    public static final E empty = null;

    public interface P<// ¢$2+k$¢ generic arguments:
    Top extends Γ, Rest extends JS<?, ?, ?>, J_γ1 extends JS<?, ?, ?>, J_γ2 extends JS<?, ?, ?>>
        extends Pʹ<Top, Rest, J_γ1, J_γ2, P<Top, Rest, J_γ1, J_γ2>> {
    }
  }



  static C<?, E> pjs(E _) {
    return null;
  }
  static <T extends Γ, R extends JS<?, ?, ?>, J_γ1 extends JS<?, ?, ?>, J_γ2 extends JS<?, ?, ?>> C<T, P<T, R, J_γ1, J_γ1>> pjs(
      P<T, R, J_γ1, J_γ2> _) {
    return null;
  }
  interface C<T extends Γʹ, R extends JS<?, ?, ?>> {
    C<?, ?> σ1();
    C<?, ?> σ2();
    C<?, ?> $();
  }
  interface Cγ1<C extends JS<?, ?, ?>> extends C<γ1, JS<?, ?, ?>> {
    C<γ2, ?> σ1();
    C<?, ?> σ2();
  }

  interface Cγ2 extends C<γ2, JS<?, ?, ?>> {
    C<?, ?> σ1();
    C<?, ?> σ2();
  }
  static C<γ1,P<γ1, E, E, ¤> > build = null;
  static void use_cases() {
    A.build.σ1();
  }

  static void pjsing_into_a_stack_use_cases() {
    P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, P<γ1, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>> _1 = JS.empty
        .γ2().γ1().γ2().γ1().γ2();
    C<γ2, P<γ2, P<γ1, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ2, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, E, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>, P<γ1, P<γ2, E, ¤, E>, P<γ2, E, ¤, E>, E>>> _2 = pjs(
        _1);
    E _3 = JS.empty;
    C<?, E> _4 = pjs(_3);

  }
}
