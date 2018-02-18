package org.spartan.fajita.revision.bnf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;

public class DerivationRule {
  public final NonTerminal lhs;
  private final List<Symbol> rhs;

  public DerivationRule(final NonTerminal lhs, final List<Symbol> rhs) {
    this.lhs = lhs;
    this.rhs = new ArrayList<>(rhs);
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(lhs.toString2() + " ::= ");
    for (Symbol symb : rhs)
      sb.append(symb.toString() + " ");
    return sb.toString();
  }
  @Override public boolean equals(final Object obj) {
    return obj != null && obj.getClass().equals(getClass()) && lhs.equals(((DerivationRule) obj).lhs)
        && rhs.equals(((DerivationRule) obj).rhs);
  }
  @Override public int hashCode() {
    return lhs.hashCode() + 7 * rhs.hashCode();
  }
  public Symbol get(int i) {
    return rhs.get(i);
  }
  public int size() {
    return rhs.size();
  }
  public List<Symbol> getRHS() {
    return new ArrayList<>(rhs);
  }
  public List<Symbol> getRHSSuffix(int from) {
    return getRHS().subList(from, rhs.size());
  }
  public static DerivationRule of(NonTerminal lhs, Symbol... rhs) {
    return new DerivationRule(lhs, Arrays.asList(rhs));
  }
}