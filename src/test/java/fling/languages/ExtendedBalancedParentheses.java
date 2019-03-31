package fling.languages;

import static fling.automata.DPDA.dpda;
import static fling.generated.ExtendedBalancedParentheses.__;
import static fling.grammar.sententials.Alphabet.ε;
import static fling.languages.ExtendedBalancedParentheses.Q.q0;
import static fling.languages.ExtendedBalancedParentheses.Q.q1;
import static fling.languages.ExtendedBalancedParentheses.Q.q2;
import static fling.languages.ExtendedBalancedParentheses.Γ.γ0;
import static fling.languages.ExtendedBalancedParentheses.Γ.γ1;
import static fling.languages.ExtendedBalancedParentheses.Σ.c;
import static fling.languages.ExtendedBalancedParentheses.Σ.Ↄ;
import static fling.languages.ExtendedBalancedParentheses.Σ.ↄ;

import fling.adapters.JavaAPIAdapter;
import fling.automata.DPDA;
import fling.compiler.api.APICompiler;
import fling.grammar.Grammar;
import fling.grammar.sententials.Named;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Verb;
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
  public static final String fluentAPI = new JavaAPIAdapter("fling.generated", "ExtendedBalancedParentheses", "__", "$",
      new NaiveNamer("fling.generated", "ExtendedBalancedParentheses")) //
          .printFluentAPI(new APICompiler(dpda).compileFluentAPI());

  public static void compilationTest() {
    __().c().ↄ().$();
    __().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ();
    __().c().c().c().ↄ().ↄ().ↄ().$();
    __().c().c().c().ↄ().Ↄ().c().ↄ().$();
    __().c().c().c().ↄ().Ↄ().c();
    __().c().c().c().ↄ().Ↄ().c().Ↄ().$();
  }
}
