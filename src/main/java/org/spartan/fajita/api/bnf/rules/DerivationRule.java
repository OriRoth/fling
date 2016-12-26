package org.spartan.fajita.api.bnf.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class DerivationRule {
  public final NonTerminal lhs;
  private final List<Symbol> expression;

  public DerivationRule(final NonTerminal lhs, final List<Symbol> expression) {
    this.lhs = lhs;
    this.expression = new ArrayList<>(expression.stream().collect(Collectors.toList()));
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(lhs.toString2() + " ::= ");
    for (Symbol symb : expression)
      sb.append(symb.toString() + " ");
    return sb.toString();
  }
  // TODO: change all horrific access to children using the new get() method.
  public List<Symbol> getChildren() {
    return new ArrayList<>(expression);
  }
  @Override public boolean equals(final Object obj) {
    return obj.getClass() == getClass() && lhs.equals(((DerivationRule) obj).lhs)
        && getChildren().equals(((DerivationRule) obj).getChildren());
  }
  @Override public int hashCode() {
    return lhs.hashCode() + 7 * getChildren().hashCode();
  }
  public Symbol get(int dotIndex) {
    return getChildren().get(dotIndex);
  }
}