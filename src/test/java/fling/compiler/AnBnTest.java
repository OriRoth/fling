package fling.compiler;

import static fling.automata.DPDA.dpda;
import static fling.compiler.AnBnTest.Q.*;
import static fling.compiler.AnBnTest.Γ.*;
import static fling.compiler.AnBnTest.Σ.*;
import static fling.sententials.Alphabet.ε;
import static fling.generated.AnBn.__;

import fling.automata.DPDA;

public class AnBnTest {
  enum Q {
    q0, q1, q2
  }

  enum Σ {
    a, b
  }

  enum Γ {
    E, X
  }

  public static final DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q2) //
      .γ0(E) //
      .δ(q0, a, E, q0, E, X) //
      .δ(q0, a, X, q0, X, X) //
      .δ(q0, b, X, q1) //
      .δ(q1, b, X, q1) //
      .δ(q1, ε(), E, q2) //
      .go();
  public static final String JavaFluentAPI = new JavaAdapter<Q, Σ, Γ>("fling.generated", "AnBn", "__", "$") //
      .printFluentAPI(new Compiler<>(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAdapter<Q, Σ, Γ>("__", "$") //
      .printFluentAPI(new Compiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().a().a().a().b().b().b().$();
    __().a().a().a().b().b();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
