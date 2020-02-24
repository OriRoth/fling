package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.V.P;
import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.adapters.JavaMediator;

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
      "il.ac.technion.cs.fling.examples.generated", "BalancedParentheses", Σ.class);
}
