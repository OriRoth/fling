package fling.grammars.api;

import fling.internal.grammar.sententials.Terminal;
import fling.internal.grammar.sententials.Variable;

public class BNF {
  public enum Σ implements Terminal {
    bnf, start, derive, specialize, to, into, toEpsilon
  }

  public enum V implements Variable {
    Specification, Rule, DerivationTarget
  }
}
