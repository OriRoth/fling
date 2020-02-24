package il.ac.technion.cs.fling.examples.languages;

import static il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.V.*;
import static il.ac.technion.cs.fling.examples.languages.TaggedBalancedParentheses.Σ.*;
import static il.ac.technion.cs.fling.grammars.api.BNFAPI.bnf;

import il.ac.technion.cs.fling.*;
import il.ac.technion.cs.fling.BNF;
import il.ac.technion.cs.fling.adapters.JavaMediator;

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
      derive(AB).to(Symbol.oneOrMore(b.with(int.class))). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "il.ac.technion.cs.fling.examples.generated", "TaggedBalancedParentheses", Σ.class);
}
