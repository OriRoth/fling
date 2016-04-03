package org.spartan.fajita.api.bnf.symbols;

public interface Symbol {
  public String name();
  public default boolean isVerb() {
    return Verb.class.isAssignableFrom(getClass());
  }
  public default boolean isNonTerminal() {
    return NonTerminal.class.isAssignableFrom(getClass());
  }
}