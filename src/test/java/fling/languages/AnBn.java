package fling.languages;

import static fling.automata.DPDA.dpda;
import static fling.sententials.Alphabet.ε;
import static fling.generated.AnBn.__;
import static fling.languages.AnBn.Q.*;
import static fling.languages.AnBn.Γ.*;
import static fling.languages.AnBn.Σ.*;

import fling.automata.DPDA;
import fling.compiler.Compiler;
import fling.compiler.CppAdapter;
import fling.compiler.JavaAdapter;

public class AnBn {
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
