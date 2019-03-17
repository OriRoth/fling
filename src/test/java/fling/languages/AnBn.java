package fling.languages;

import static fling._generated.AnBn.__;
import static fling.automata.DPDA.dpda;

import java.util.EnumSet;
import java.util.LinkedHashSet;

import static fling.grammar.sententials.Alphabet.ε;
import static fling.languages.AnBn.Q.*;
import static fling.languages.AnBn.Γ.*;
import static fling.languages.AnBn.Σ.*;

import fling.adapters.CppAPIAdapter;
import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compiler.api.APICompiler;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;

public class AnBn {
  enum Q implements Named {
    q0, q1, q2
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
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
  public static final String JavaFluentAPI = new JavaAPIAdapter<Q, Σ, Γ>(new LinkedHashSet<>(EnumSet.allOf(Σ.class)),
      "fling.generated", "AnBn", "__", "$") //
          .printFluentAPI(new APICompiler<>(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter<Q, Σ, Γ>("__", "$") //
      .printFluentAPI(new APICompiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().a().a().a().b().b().b().$();
    __().a().a().a().b().b();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
