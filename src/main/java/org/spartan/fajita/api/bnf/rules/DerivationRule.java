package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class DerivationRule extends Rule {
  private final List<Symbol> expression;

  public DerivationRule(final NonTerminal lhs, final List<Symbol> expression, final int index) {
    super(lhs, index);
    this.expression = new ArrayList<>(expression.stream().filter(s -> s != SpecialSymbols.epsilon).collect(Collectors.toList()));
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(lhs.methodSignatureString() + " ::= ");
    for (Symbol symb : expression)
      sb.append(symb.methodSignatureString() + " ");
    return sb.toString();
  }
  @Override public List<Symbol> getChildren() {
    return new ArrayList<>(expression);
  }
}