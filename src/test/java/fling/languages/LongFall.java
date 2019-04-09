package fling.languages;

import static fling.automata.Alphabet.ε;
import static fling.automata.DPDA.dpda;
import static fling.generated.LongFall.a;
import static fling.generated.LongFall.b;
import static fling.languages.LongFall.Q.q0;
import static fling.languages.LongFall.Q.q1;
import static fling.languages.LongFall.Γ.g0;
import static fling.languages.LongFall.Γ.g1;
import static fling.languages.LongFall.Σ.a;
import static fling.languages.LongFall.Σ.b;

import fling.adapters.CppAPIAdapter;
import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compiler.api.ReliableAPICompiler;
import fling.grammar.Grammar;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Verb;
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

  public static final DPDA<Named, Verb, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q0) //
      .γ0(g0, g1) //
      .δ(q0, a, g1, q0, g1, g1) //
      .δ(q0, b, g1, q1) //
      .δ(q1, ε(), g1, q1) //
      .δ(q1, ε(), g0, q0, g0) //
      .go());
  public static final String JavaFluentAPI = new JavaAPIAdapter("fling.generated", "LongFall", "$",
      new NaiveNamer("fling.generated", "LongFall")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("fling.generated", "LongFall")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());

  public static void compilationTest() {
    a().a().a().a().a().a().a().$();
    a().a().a().a().a().a().a().b().$();
    b().$();
  }
  public static void main(String[] args) {
    System.out.println(CppFluentAPI);
  }
}
