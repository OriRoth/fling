package fling.compiler;

import static fling.automata.DPDA.dpda;
import static fling.compiler.LongFallTest.Q.*;
import static fling.compiler.LongFallTest.Γ.*;
import static fling.compiler.LongFallTest.Σ.*;
import static fling.sententials.Alphabet.ε;
import static fling.generated.LongFall.__;

import fling.automata.DPDA;

public class LongFallTest {
  enum Q {
    q0, q1
  }

  enum Σ {
    a, b
  }

  enum Γ {
    γ0, γ1
  }

  public static final DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(γ0) //
      .δ(q0, a, γ0, q0, γ0, γ1) //
      .δ(q0, a, γ1, q0, γ1, γ1) //
      .δ(q0, b, γ1, q1) //
      .δ(q1, ε(), γ1, q1) //
      .δ(q1, ε(), γ0, q0, γ0) //
      .go();
  public static final String fluentAPI = new JavaAdapter<Q, Σ, Γ>("fling.generated", "LongFall", "__", "$") //
      .printFluentAPI(new Compiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().a().a().a().a().a().a().a().$();
    __().a().a().a().a().a().a().a().b().$();
  }
}
