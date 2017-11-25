package org.spartan.fajita.api.bnf;

import org.spartan.fajita.api.EFajita;
import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public abstract class ENonTerminal implements Symbol {
  @Override public String name() {
    return "#"; // Invalid Java identifier
  }
  public abstract Symbol head();
  public abstract ENonTerminal bind(EFajita builder, NonTerminal lhs);
}
