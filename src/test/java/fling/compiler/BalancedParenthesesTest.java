package fling.compiler;

import static fling.automata.DPDA.dpda;
import static fling.compiler.BalancedParenthesesTest.Q.q0;
import static fling.compiler.BalancedParenthesesTest.Q.q1;
import static fling.compiler.BalancedParenthesesTest.Q.q2;
import static fling.compiler.BalancedParenthesesTest.Γ.γ0;
import static fling.compiler.BalancedParenthesesTest.Γ.γ1;
import static fling.compiler.BalancedParenthesesTest.Σ.c;
import static fling.compiler.BalancedParenthesesTest.Σ.Ↄ;
import static fling.compiler.BalancedParenthesesTest.Σ.ↄ;
import static fling.sententials.Alphabet.ε;
import static fling.generated.BalancedParentheses.__;

import fling.automata.DPDA;

public class BalancedParenthesesTest {
  enum Q {
    q0, q1, q2
  }

  enum Σ {
    c, ↄ, Ↄ
  }

  enum Γ {
    γ0, γ1
  }

  public static final DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(γ0) //
      .δ(q0, c, γ0, q1, γ0, γ1) //
      .δ(q1, c, γ1, q1, γ1, γ1) //
      .δ(q1, ↄ, γ1, q1) //
      .δ(q1, ε(), γ0, q0, γ0) //
      .δ(q1, Ↄ, γ1, q2) //
      .δ(q2, ε(), γ1, q2) //
      .δ(q2, ε(), γ0, q0, γ0) //
      .go();
  public static final String fluentAPI = new JavaAdapter<Q, Σ, Γ>("fling.generated", "BalancedParentheses", "__", "$") //
      .printFluentAPI(new Compiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().c().ↄ().$();
    __().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ().ↄ().$();
    __().c().c().c().ↄ().Ↄ().c().ↄ().$();
    __().c().c().c().ↄ().Ↄ().c();
    __().c().c().c().ↄ().Ↄ().c().Ↄ().$();
  }
}
