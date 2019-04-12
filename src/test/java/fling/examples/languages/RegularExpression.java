package fling.examples.languages;

import static fling.examples.languages.RegularExpression.V.*;
import static fling.examples.languages.RegularExpression.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;

import fling.*;
import fling.adapters.JavaMediator;
import fling.internal.grammar.sententials.*;

public class RegularExpression {
  public enum Σ implements Terminal {
    re, exactly, option, noneOrMore, oneOrMore, either, anyChar, anyDigit, and, or
  }

  public enum V implements Variable {
    Expression, RE, Tail
  }

  public static final fling.BNF bnf = bnf(). //
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
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "RegularExpression", Σ.class);
}
