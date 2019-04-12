package fling.examples.automata;

import static fling.DPDA.dpda;
import static fling.automata.Alphabet.ε;
import static fling.examples.automata.LongFall.Q.*;
import static fling.examples.automata.LongFall.Γ.*;
import static fling.examples.automata.LongFall.Σ.*;

import fling.*;
import fling.adapters.*;
import fling.compilers.api.ReliableAPICompiler;
import fling.internal.grammar.Grammar;
import fling.internal.grammar.sententials.*;
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
  public static final String JavaFluentAPI = new JavaAPIAdapter("fling.examples.generated", "LongFall", "$",
      new NaiveNamer("fling.examples.generated", "LongFall")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
  public static final String CppFluentAPI = new CppAPIAdapter("$", new NaiveNamer("LongFall")) //
      .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
}
