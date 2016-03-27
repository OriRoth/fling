package org.spartan.fajita.api.bnf.symbols;

public class SpecialSymbols {
  // TODO: make $,augmentedStartSymbol not visible to end user.
  public static final Verb $ = new Verb("$") {
    @Override public String toString() {
      return "$";
    }
  };
  public static final NonTerminal augmentedStartSymbol = new NonTerminal() {
    @Override public String name() {
      return "S'";
    }
    @Override public String toString() {
      return name();
    }
  };
}
