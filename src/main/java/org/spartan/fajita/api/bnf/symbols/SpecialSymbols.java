package org.spartan.fajita.api.bnf.symbols;

public class SpecialSymbols {
  // TODO: i think no one really uses it since an epsilon rule is denoted by a
  // rule with NO children
  public static final Symbol epsilon = new Symbol() {
    @Override public String name() {
      return "\u03B5"; // epsilon symbol
    }
    @Override public String toString() {
      return name();
    }
  };
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
