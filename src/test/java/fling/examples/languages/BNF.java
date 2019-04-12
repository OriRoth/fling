package fling.examples.languages;

import static fling.examples.languages.BNF.V.*;
import static fling.examples.languages.BNF.Σ.*;
import static fling.grammars.api.BNFAPI.bnf;
import static fling.internal.grammar.sententials.Notation.noneOrMore;

import fling.adapters.JavaMediator;
import fling.internal.grammar.sententials.*;

public class BNF {
  @SuppressWarnings("hiding") public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon
  }

  public enum V implements Variable {
    Specification, Rule, DerivationTarget
  }

  public static final fling.grammars.BNF bnf = bnf(). //
      start(Specification). //
      derive(Specification).to(Σ.bnf, start.with(Variable.class), noneOrMore(Rule)). //
      derive(Rule).to(derive.with(Variable.class), DerivationTarget). //
      derive(DerivationTarget).to(to.many(Symbol.class)). //
      derive(DerivationTarget).to(toEpsilon). //
      derive(Rule).to(specialize.with(Variable.class), into.many(Variable.class)). //
      build();
  public static final JavaMediator jm = new JavaMediator(bnf, //
      "fling.examples.generated", "BNF", Σ.class);
}
