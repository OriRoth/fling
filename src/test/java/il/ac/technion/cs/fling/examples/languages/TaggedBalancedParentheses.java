package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.V.*;
import static il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.*;

public class TaggedBalancedParentheses implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    c, ↄ, a, b
  }

  public enum V implements Variable {
    P, AB
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
        derive(P).to(c.many(char.class), P, ↄ.with(AB), P). //
        derive(P).toEpsilon(). //
        derive(AB).to(a). //
        derive(AB).to(Symbol.oneOrMore(b.with(int.class))). //
        build();
  }
}
