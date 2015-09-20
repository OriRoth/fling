package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
  @Override public default String methodSignatureString() {
    return "<" + name() + ">";
  }
}