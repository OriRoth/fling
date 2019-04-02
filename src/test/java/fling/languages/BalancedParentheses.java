package fling.languages;

import static fling.generated.BalancedParentheses._1;
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
      derive(P, c, P, ↄ, P). //
      derive(P). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "BalancedParentheses", Σ.class);

  public static void compilationTest() {
    _1().c().ↄ().$();
    _1().c().ↄ().ↄ();
    _1().c().c().c().ↄ().ↄ();
    _1().c().c().c().ↄ().ↄ().ↄ().$();
    _1().c().c().ↄ().ↄ().c().ↄ().$();
    _1().c().c().ↄ().ↄ().c();
    _1().ↄ();
  }
}
