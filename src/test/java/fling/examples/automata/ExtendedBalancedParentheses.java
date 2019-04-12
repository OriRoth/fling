package fling.examples.automata;

import static fling.automata.Alphabet.ε;
import static fling.automata.DPDA.dpda;
import static fling.examples.automata.ExtendedBalancedParentheses.Q.*;
import static fling.examples.automata.ExtendedBalancedParentheses.Γ.*;
import static fling.examples.automata.ExtendedBalancedParentheses.Σ.*;

import fling.Terminal;
import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compilers.api.ReliableAPICompiler;
import fling.internal.grammar.Grammar;
import fling.internal.grammar.sententials.*;
import fling.namers.NaiveNamer;

public class ExtendedBalancedParentheses {
  enum Q implements Named {
    q0, q1, q2
  }

  enum Σ implements Terminal {
    c, ↄ, Ↄ
  }

  enum Γ implements Named {
    γ0, γ1
  }

  public static final DPDA<Named, Verb, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
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
      .go());
  public static final String fluentAPI = new JavaAPIAdapter("fling.examples.generated", "ExtendedBalancedParentheses", "$",
      new NaiveNamer("fling.examples.generated", "ExtendedBalancedParentheses")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
}
