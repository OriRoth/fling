package fling.examples.languages;

import static fling.examples.languages.BalancedParentheses.V.P;
import static fling.examples.languages.BalancedParentheses.Σ.c;
import static fling.examples.languages.BalancedParentheses.Σ.ↄ;
import static fling.grammars.api.BNFAPI.bnf;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.Terminal;
import fling.internal.grammar.sententials.Variable;

public class BalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ
  }

  public enum V implements Variable {
    P
  }

  public static final BNF bnf = bnf(). //
      start(P). //
      derive(P).to(c, P, ↄ, P). //
      derive(P).toEpsilon(). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "BalancedParentheses", Σ.class);
}
