package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Γ.X;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Γ.Y;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Σ.a;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Σ.b;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Σ.c;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Σ.d;
import static il.ac.technion.cs.fling.examples.languages.QuantifiersTestLanguage.Σ.e;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.noneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.oneOrMore;
import static il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers.optional;

import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public class QuantifiersTestLanguage
    implements FluentLanguageAPI<QuantifiersTestLanguage.Σ, QuantifiersTestLanguage.Γ> {
  public enum Σ implements Terminal {
    a, b, c, d, e
  }

  public enum Γ implements Variable {
    X, Y
  }

  @Override public Class<Σ> Σ() {
    return Σ.class;
  }

  @Override public Class<Γ> Γ() {
    return Γ.class;
  }

  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    return bnf(). //
        start(X). //
        derive(X).to(oneOrMore(a.with(int.class)), //
            noneOrMore(b.with(int.class)), //
            optional(Y), //
            optional(e.with(int.class)))
        . //
        derive(Y).to(c.with(int.class)). //
        or(d.with(int.class)). //
        build();
  }
}
