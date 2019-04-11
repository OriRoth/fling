package fling.examples.automata;

import static fling.automata.Alphabet.ε;
import static fling.automata.DPDA.dpda;
import static fling.examples.automata.AnBn.Q.q0;
import static fling.examples.automata.AnBn.Q.q1;
import static fling.examples.automata.AnBn.Q.q2;
import static fling.examples.automata.AnBn.Γ.E;
import static fling.examples.automata.AnBn.Γ.X;
import static fling.examples.automata.AnBn.Σ.a;
import static fling.examples.automata.AnBn.Σ.b;

import fling.adapters.CppAPIAdapter;
import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compilers.api.ReliableAPICompiler;
import fling.internal.grammar.Grammar;
import fling.internal.grammar.sententials.Named;
import fling.internal.grammar.sententials.Terminal;
import fling.internal.grammar.sententials.Verb;
import fling.namers.NaiveNamer;

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

  public static final DPDA<Named, Verb, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
      .q0(q0) //
      .F(q2) //
      .γ0(E) //
      .δ(q0, a, E, q0, E, X) //
      .δ(q0, a, X, q0, X, X) //
      .δ(q0, b, X, q1) //
      .δ(q1, b, X, q1) //
      .δ(q1, ε(), E, q2) //
      .go());
  public static final String JavaFluentAPI = new JavaAPIAdapter("fling.examples.generated", "AnBn", "$",
      new NaiveNamer("fling.examples.generated", "AnBn")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("AnBn")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
}
