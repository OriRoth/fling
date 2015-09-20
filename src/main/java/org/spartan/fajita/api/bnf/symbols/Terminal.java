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
      methodSig += clss.getSimpleName() + ",";
    methodSig = methodSig.substring(0, methodSig.length() - 1);
    return methodSig + ")";
  }

  public static final Terminal $ = new Terminal() {
    @Override public String name() {
      return "$";
    }
    @Override public String toString() {
      return name();
    }
    @Override public Type type() {
      return Type.notype;
    }
  };
}