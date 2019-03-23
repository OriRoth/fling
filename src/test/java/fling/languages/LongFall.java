package fling.languages;

import static fling.automata.DPDA.dpda;
import static fling.generated.LongFall.__;
import static fling.grammar.sententials.Alphabet.ε;
import static fling.languages.LongFall.Q.q0;
import static fling.languages.LongFall.Q.q1;
import static fling.languages.LongFall.Γ.g0;
import static fling.languages.LongFall.Γ.g1;
import static fling.languages.LongFall.Σ.a;
import static fling.languages.LongFall.Σ.b;

import fling.adapters.CppAPIAdapter;
import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compiler.api.APICompiler;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.namers.NaiveNamer;

public class LongFall {
  enum Q implements Named {
    q0, q1
  }

  enum Σ implements Terminal {
    a, b
  }

  enum Γ implements Named {
    g0, g1
  }

  public static final DPDA<Q, Σ, Γ> dpda = dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(g0, g1) //
      .δ(q0, a, g1, q0, g1, g1) //
      .δ(q0, b, g1, q1) //
      .δ(q1, ε(), g1, q1) //
      .δ(q1, ε(), g0, q0, g0) //
      .go();
  public static final String JavaFluentAPI = new JavaAPIAdapter<Q, Σ, Γ>("fling.generated", "LongFall", "__", "$", new NaiveNamer()) //
      .printFluentAPI(new APICompiler<>(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter<Q, Σ, Γ>("__", "$", new NaiveNamer()) //
      .printFluentAPI(new APICompiler<>(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().a().a().a().a().a().a().a().$();
    __().a().a().a().a().a().a().a().b().$();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
