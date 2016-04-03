package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
  public default String toString2() {
    return "<" + name() + ">";
  }
//  @Override public default String serialize() {
//    return "<" + name() + ">";
//  }
//  public static NonTerminal deserialize(String signature) {
//    return () -> signature.substring(1, signature.length() - 1);
//  }
}