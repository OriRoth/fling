package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.RegularExpression.V.*;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.RegularExpression.*;

public class RegularExpression implements FluentLanguageAPI<Σ, V> {
  public enum Σ implements Terminal {
    re, exactly, option, noneOrMore, oneOrMore, either, anyChar, anyDigit, and, or
  }

  public enum V implements Variable {
    Expression, RE, Tail
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<V> V() {
    return V.class;
  }
  @Override public il.ac.technion.cs.fling.FancyEBNF BNF() {
    return bnf(). //
        start(Expression). //
        derive(Expression).to(re, RE). //
        derive(RE).to(exactly.with(String.class), Tail). //
        or(option.with(RE), Tail). //
        or(noneOrMore.with(RE), Tail). //
        or(oneOrMore.with(RE), Tail). //
        or(either.many(RE), Tail). //
        or(anyChar, Tail). //
        or(anyDigit, Tail). //
        derive(Tail).to(and, RE).or(or, RE).orNone(). //
        build();
  }
}
