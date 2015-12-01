package org.spartan.fajita.api.bnf.symbols;

public interface Terminal extends Symbol {
  public static Terminal deserialize(String signature) {
    return () -> signature.substring(1, signature.length() - 1);
  }
}
