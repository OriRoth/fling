package org.spartan.fajita.api.bnf.symbols;

public interface Symbol {
  public String name();
  public String methodSignatureString();
  public default boolean isTerminal() {
    return Terminal.class.isAssignableFrom(getClass());
  }
  public default boolean isNonTerminal() {
    return NonTerminal.class.isAssignableFrom(getClass());
  }
  public default String serialize() {
    return "^" + methodSignatureString() + "^";
  }
  public static Symbol deserialize(String signature) {
    return new Symbol(){

      @Override public String name() {
        return signature;
      }

      @Override public String methodSignatureString() {
        return signature;
      }
      
    };
  }
}