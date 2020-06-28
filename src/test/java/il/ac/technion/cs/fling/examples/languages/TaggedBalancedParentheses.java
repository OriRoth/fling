package il.ac.technion.cs.fling.examples.languages;
import static il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Γ.*;
import static il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;
import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Γ;
import il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.*;
public class TaggedBalancedParentheses implements FluentLanguageAPI<Σ, Γ> {
  public enum Σ implements Terminal {
    c, ↄ, a, b
  }
  public enum Γ implements Variable {
    P, AB
  }
  @Override public Class<Σ> Σ() {
    return Σ.class;
  }
  @Override public Class<Γ> Γ() {
    return Γ.class;
  }
  @Override public il.ac.technion.cs.fling.EBNF BNF() {
    return bnf(). //
        start(P). //
        derive(P).to(c.many(char.class), P, ↄ.with(AB), P). //
        derive(P).toEpsilon(). //
        derive(AB).to(a). //
        derive(AB).to(Quantifiers.oneOrMore(b.with(int.class))). //
        build();
  }
}
