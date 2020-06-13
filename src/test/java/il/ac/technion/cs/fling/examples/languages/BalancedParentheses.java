package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Γ.P;
import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Σ.c;
import static il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Σ.ↄ;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.examples.FluentLanguageAPI;
import il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Γ;
import il.ac.technion.cs.fling.examples.languages.BalancedParentheses.Σ;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public class BalancedParentheses implements FluentLanguageAPI<Σ, Γ> {
  public enum Σ implements Terminal {
    c, ↄ
  }

  public enum Γ implements Variable {
    P
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
        derive(P).to(c, P, ↄ, P). //
        derive(P).toEpsilon(). //
        build();
  }
}
