package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.RegularExpression.V.*;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.adapters.JavaMediator;

public class RegularExpression {
  public enum Σ implements Terminal {
    re, exactly, option, noneOrMore, oneOrMore, either, anyChar, anyDigit, and, or
  }

  public enum V implements Variable {
    Expression, RE, Tail
  }

  public static final il.ac.technion.cs.fling.BNF bnf = bnf(). //
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
      "il.ac.technion.cs.fling.examples.generated", "RegularExpression", Σ.class);
}
