package org.spartan.fajita.api.bnf.symbols;

public interface NonTerminal extends Symbol {
  @Override public default String methodSignatureString() {
    return "<" + name() + ">";
  }
  
  @Override public default String serialize() {
    return methodSignatureString();
  }
  public static NonTerminal deserialize(String signature) {
    return new NonTerminal() {
      @Override public String name() {
        return signature.substring(1, signature.length()-1);
      }
      @Override public String methodSignatureString() {
        return signature;
      }
    };
  }
}