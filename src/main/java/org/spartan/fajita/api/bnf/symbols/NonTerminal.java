package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
  default String name(String packagePath) {
    return packagePath + "." + name();
  }
  default String name(String packagePath, String containingClass) {
    return packagePath + "." + containingClass + "." + name();
  }
  public default String toString2() {
    return "<" + name() + ">";
  }
}