package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.V.P;
import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.BalancedParentheses.*;

public class BalancedParentheses implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    c, ↄ
  }

  public enum V implements Variable {
    P
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<V> V() {
    return V.class;
  }
  @Override public il.ac.technion.cs.fling.BNF BNF() {
    return bnf(). //
        start(P). //
        derive(P).to(c, P, ↄ, P). //
        derive(P).toEpsilon(). //
        build();
  }
}
