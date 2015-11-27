package org.spartan.fajita.api.bnf.symbols;

import java.util.List;

public interface Terminal extends Symbol {
  public Type type();
  @Override public default String methodSignatureString() {
    List<Class<?>> classes = type().classes;
    if (classes.size() == 1 && classes.iterator().next() == Void.class)
      return name() + "()";
    String methodSig = name() + "(";
    for (Class<?> clss : classes)
      methodSig += clss.getName() + ",";
    methodSig = methodSig.substring(0, methodSig.length() - 1);
    return methodSig + ")";
  }
  @Override default String serialize() {
    return "%"+methodSignatureString()+"%";
  }
  public static Terminal deserialize(String signature) {
    return new Terminal() {
      @Override public String name() {
        return signature.substring(1, signature.indexOf('('));
      }
      @Override public String methodSignatureString() {
        return signature.substring(1,signature.length()-1);
      }
      @Override public Type type() {
        return null;
      }
    };
  }
}