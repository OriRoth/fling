package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Γ.Expression;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Γ.RE;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Γ.Tail;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.and;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyChar;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.anyDigit;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.either;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.exactly;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.noneOrMore;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.oneOrMore;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.option;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.or;
import static il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ.re;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.RegularExpression.Γ;
import il.ac.technion.cs.fling.examples.languages.RegularExpression.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class RegularExpression implements FluentLanguageAPI<Σ, Γ> {
  public enum Σ implements Terminal {
    re, exactly, option, noneOrMore, oneOrMore, either, anyChar, anyDigit, and, or
  }
  public enum Γ implements Variable {
    Expression, RE, Tail
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
  }
  @Override public il.ac.technion.cs.fling.EBNF BNF() {
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
