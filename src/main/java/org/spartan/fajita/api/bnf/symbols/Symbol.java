package org.spartan.fajita.api.bnf.symbols;

public interface Symbol {
  public String name();
  public default boolean isVerb() {
    return Verb.class.isAssignableFrom(getClass());
  }
  public default boolean isNonTerminal() {
    return NonTerminal.class.isAssignableFrom(getClass());
  }
//  public default String serialize() {
//    return "^" + name() + "^";
//  }
//  public static Symbol deserialize(String signature) {
//    return new Symbol() {
//      @Override public String name() {
//        return signature.substring(1, signature.length() - 1);
//      }
//    };
//  }
}