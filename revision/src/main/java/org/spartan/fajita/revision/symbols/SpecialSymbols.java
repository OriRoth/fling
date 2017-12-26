package org.spartan.fajita.revision.symbols;

public class SpecialSymbols {
  public static final Verb $ = new Verb(new Terminal() {
    @Override public String name() {
      return "$";
    }
  }) {
    @Override public String toString() {
      return "$";
    }
  };
  public static final Verb empty = new Verb(new Terminal() {
    @Override public String name() {
      return "~";
    }
  }) {
    @Override public String toString() {
      return "~";
    }
  };
  public static final NonTerminal augmentedStartSymbol = new NonTerminal() {
    @Override public String name() {
      return "augS";
    }
    @Override public String toString() {
      return name();
    }
  };
}
