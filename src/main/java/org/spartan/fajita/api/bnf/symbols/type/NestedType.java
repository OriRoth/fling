package org.spartan.fajita.api.bnf.symbols.type;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;

public class NestedType implements ParameterType {
  public final NonTerminal nested;

  public NestedType(NonTerminal nested) {
    this.nested = nested;
  }
}
