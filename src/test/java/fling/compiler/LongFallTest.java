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
    g0, g1
  }

  public static final DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(g0) //
      .δ(q0, a, g0, q0, g0, g1) //
      .δ(q0, a, g1, q0, g1, g1) //
      .δ(q0, b, g1, q1) //
      .δ(q1, ε(), g1, q1) //
      .δ(q1, ε(), g0, q0, g0) //
      .go();
  public static final String JavaFluentAPI = new JavaAdapter<Q, Σ, Γ>("fling.generated", "LongFall", "__", "$") //
      .printFluentAPI(new Compiler<>(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAdapter<Q, Σ, Γ>("__", "$") //
      .printFluentAPI(new Compiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().a().a().a().a().a().a().a().$();
    __().a().a().a().a().a().a().a().b().$();
  }

  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
