package org.spartan.fajita.api.ast;

import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Terminal;

public class Atomic extends AbstractNode {

  private final Terminal term;
  public Atomic(Terminal term) {
    this.term = term;
  }
  @Override public Symbol getSymbol() {
    return term;
  }
}
