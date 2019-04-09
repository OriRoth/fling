package fling.languages;

import static fling.grammar.BNF.bnf;
import static fling.grammar.sententials.Notation.oneOrMore;
import static fling.languages.TaggedBalancedParentheses.V.AB;
import static fling.languages.TaggedBalancedParentheses.V.P;
import static fling.languages.TaggedBalancedParentheses.Σ.a;
import static fling.languages.TaggedBalancedParentheses.Σ.b;
import static fling.languages.TaggedBalancedParentheses.Σ.c;
import static fling.languages.TaggedBalancedParentheses.Σ.ↄ;

import fling.adapters.JavaMediator;
import fling.grammar.BNF;
import fling.grammar.sententials.Terminal;
import fling.grammar.sententials.Variable;

public class TaggedBalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ, a, b
  }

  public enum V implements Variable {
    P, AB
  }

  public static final BNF bnf = bnf(V.class). //
      start(P). //
      derive(P).to(c.many(char.class), P, ↄ.with(AB), P). //
      derive(P).toEpsilon(). //
      derive(AB).to(a). //
      derive(AB).to(oneOrMore(b.with(int.class))). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.generated", "TaggedBalancedParentheses", Σ.class);
}
