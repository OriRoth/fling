/**
 * If you see compilation errors in the import section of this file, please run
 * {@link RunMeFirstToGenerateTests} to generate the fluent API that this test
 * checks. After running this file, please refresh the project.
 */
package fling.examples;

import static fling.examples.BalancedParentheses.V.P;
import static fling.examples.BalancedParentheses.Σ.c;
import static fling.examples.BalancedParentheses.Σ.ↄ;

import static fling.generated.BalancedParentheses.c;
import static fling.grammar.BNF.bnf;

import fling.adapters.JavaMediator;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public class BalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ
  }

  public enum V implements Variable {
    P
  }

  public static final BNF bnf = bnf(V.class). //
      start(P). //
      derive(P, c, P, ↄ, P). //
      derive(P). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "BalancedParentheses", Σ.class);

  public static void compilationTest() {
    c().ↄ().$();
    c().ↄ().ↄ();
    c().c().c().ↄ().ↄ();
    c().c().c().ↄ().ↄ().ↄ().$();
    c().c().ↄ().ↄ().c().ↄ().$();
    c().c().ↄ().ↄ().c();
    // ↄ();
  }
}
