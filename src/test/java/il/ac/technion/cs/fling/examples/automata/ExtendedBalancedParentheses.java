package il.ac.technion.cs.fling.examples.automata;

import static il.ac.technion.cs.fling.DPDA.dpda;
import static il.ac.technion.cs.fling.automata.Alphabet.ε;
import static il.ac.technion.cs.fling.examples.automata.ExtendedBalancedParentheses.Q.*;
import static il.ac.technion.cs.fling.examples.automata.ExtendedBalancedParentheses.Γ.*;
import static il.ac.technion.cs.fling.examples.automata.ExtendedBalancedParentheses.Σ.*;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.JavaAPIAdapter;
import il.ac.technion.cs.fling.compilers.api.ReliableAPICompiler;
import il.ac.technion.cs.fling.internal.grammar.Grammar;
import il.ac.technion.cs.fling.internal.grammar.sententials.Token;
import il.ac.technion.cs.fling.namers.NaiveNamer;

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

  public static final DPDA<Named, Token, Named> dpda = Grammar.cast(dpda(Q.class, Σ.class, Γ.class) //
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
  public static final String fluentAPI = new JavaAPIAdapter("il.ac.technion.cs.fling.examples.generated", "ExtendedBalancedParentheses", "$",
      new NaiveNamer("il.ac.technion.cs.fling.examples.generated", "ExtendedBalancedParentheses")) //
          .printFluentAPI(new ReliableAPICompiler(dpda).compileFluentAPI());
}
