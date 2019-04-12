package fling.examples.languages;

import static fling.examples.languages.TaggedBalancedParentheses.V.*;
import static fling.examples.languages.TaggedBalancedParentheses.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;
import static fling.internal.grammar.sententials.Notation.oneOrMore;

import fling.*;
import fling.BNF;
import fling.adapters.JavaMediator;
import fling.internal.grammar.sententials.*;

public class TaggedBalancedParentheses {
  public enum Σ implements Terminal {
    c, ↄ, a, b
  }

  public enum V implements Variable {
    P, AB
  }

  public static final BNF bnf = bnf(). //
      start(P). //
      derive(P).to(c.many(char.class), P, ↄ.with(AB), P). //
      derive(P).toEpsilon(). //
      derive(AB).to(a). //
      derive(AB).to(oneOrMore(b.with(int.class))). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "TaggedBalancedParentheses", Σ.class);
}
