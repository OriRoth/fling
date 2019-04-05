package fling.examples;

/**
 * If you see compilation errors in the import section of this file, please run
 * {@link RunMeFirstToGenerateTests} to generate the fluent API that this test
 * checks. After running this file, please refresh the project.
 */
import static fling.DPDA.dpda;
import static fling.examples.ExtendedBalancedParentheses.Q.q0;
import static fling.examples.ExtendedBalancedParentheses.Q.q1;
import static fling.examples.ExtendedBalancedParentheses.Q.q2;
import static fling.examples.ExtendedBalancedParentheses.Γ.γ0;
import static fling.examples.ExtendedBalancedParentheses.Γ.γ1;
import static fling.examples.ExtendedBalancedParentheses.Σ.c;
import static fling.examples.ExtendedBalancedParentheses.Σ.Ↄ;
import static fling.examples.ExtendedBalancedParentheses.Σ.ↄ;
import static fling.generated.ExtendedBalancedParentheses.c;
import static fling.grammar.sententials.Alphabet.ε;

import fling.DPDA;
import fling.adapters.JavaAPIAdapter;
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
  public static final String fluentAPI = new JavaAPIAdapter("fling.generated", "ExtendedBalancedParentheses", "$",
      new NaiveNamer("fling.generated", "ExtendedBalancedParentheses")) //
          .printFluentAPI(new APICompiler(dpda).compileFluentAPI());

  public static void compilationTest() {
    c().ↄ().$();
    c().ↄ().ↄ();
    c().c().c().ↄ().ↄ();
    c().c().c().ↄ().ↄ().ↄ().$();
    c().c().c().ↄ().Ↄ().c().ↄ().$();
    c().c().c().ↄ().Ↄ().c();
    c().c().c().ↄ().Ↄ().c().Ↄ().$();
  }
}
