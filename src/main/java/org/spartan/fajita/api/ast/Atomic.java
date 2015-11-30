package org.spartan.fajita.api.ast;

import org.spartan.fajita.api.bnf.symbols.Verb;

public class Atomic extends AbstractNode {

  private final Verb term;
  public Atomic(Verb term) {
    this.term = term;
  }
  @Override public Verb getSymbol() {
    return term;
  }
}
