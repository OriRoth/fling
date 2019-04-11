package fling.examples.languages;

import static fling.examples.languages.TaggedBalancedParentheses.V.AB;
import static fling.examples.languages.TaggedBalancedParentheses.V.P;
import static fling.examples.languages.TaggedBalancedParentheses.Σ.a;
import static fling.examples.languages.TaggedBalancedParentheses.Σ.b;
import static fling.examples.languages.TaggedBalancedParentheses.Σ.c;
import static fling.examples.languages.TaggedBalancedParentheses.Σ.ↄ;
import static fling.grammars.BNF.bnf;
import static fling.internal.grammar.sententials.Notation.oneOrMore;

import fling.adapters.JavaMediator;
import fling.grammars.BNF;
import fling.internal.grammar.sententials.Terminal;
import fling.internal.grammar.sententials.Variable;

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
      "fling.examples.generated", "TaggedBalancedParentheses", Σ.class);
}
