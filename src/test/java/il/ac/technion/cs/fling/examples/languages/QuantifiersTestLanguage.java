package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.GeneralizedSymbol.*;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.V.*;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.*;

public class QuantifiersTestLanguage implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    a, b, c, d, e
  }

  public enum V implements Variable {
    X, Y
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<V> V() {
    return V.class;
  }
  @Override public FancyEBNF BNF() {
    return bnf(). //
        start(X). //
        derive(X).to(oneOrMore(a.with(int.class)), //
            noneOrMore(b.with(int.class)), //
            optional(Y), //
            optional(e.with(int.class))). //
        derive(Y).to(c.with(int.class)). //
            or(d.with(int.class)). //
        build();
  }
}
