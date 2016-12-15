package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
  public default String toString2() {
    return "<" + name() + ">";
  }
}