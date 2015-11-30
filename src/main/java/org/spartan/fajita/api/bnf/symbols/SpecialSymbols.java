package org.spartan.fajita.api.bnf.symbols;

public class SpecialSymbols {
  public static final Symbol epsilon = new Symbol() {
    @Override public String name() {
      return "\u03B5"; // epsilon symbol
    }
    @Override public String toString() {
      return name();
    }
    @Override public String methodSignatureString() {
      return name();
    }
  };
  // TODO: make $,augmentedStartSymbol not visible to end user.
  public static final Terminal $ = new Terminal() {
    @Override public String name() {
      return "$";
    }
    @Override public String toString() {
      return name();
    }
    @Override public String methodSignatureString() {
      return name();
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
