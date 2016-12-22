package org.spartan.fajita.api.bnf.symbols.type;

public class VarArgs implements ParameterType {
  public final Class<?> clazz;

  public VarArgs(Class<?> clazz) {
    this.clazz = clazz;
  }
}
