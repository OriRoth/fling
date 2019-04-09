package fling.languages;

import static fling.generated.BalancedParentheses.c;
import static fling.grammar.BNF.bnf;
import static fling.languages.BalancedParentheses.V.P;
import static fling.languages.BalancedParentheses.Σ.c;
import static fling.languages.BalancedParentheses.Σ.ↄ;

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
      derive(P).to(c, P, ↄ, P). //
      derive(P).toEpsilon(). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "BalancedParentheses", Σ.class);

  public static void compilationTest() {
    c().ↄ().$();
    // c().ↄ().ↄ();
    c().c().c().ↄ().ↄ();
    c().c().c().ↄ().ↄ().ↄ().$();
    c().c().ↄ().ↄ().c().ↄ().$();
    c().c().ↄ().ↄ().c();
    // ↄ();
  }
}
