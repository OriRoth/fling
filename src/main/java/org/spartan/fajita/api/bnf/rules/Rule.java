package org.spartan.fajita.api.bnf.rules;

import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public abstract class Rule implements Comparable<Rule> {
  public final NonTerminal lhs;
  private final int index;

  public Rule(final NonTerminal lhs, final int index) {
    this.lhs = lhs;
    this.index = index;
  }
  @Override public boolean equals(final Object obj) {
    return obj.getClass() == getClass() && lhs.equals(((Rule) obj).lhs) && getChildren().equals(((Rule) obj).getChildren());
  }
  /**
   * Because we should only one derivation rule for each nonterminal we return
   * lhs's hashcode.
   * 
   * @return lhs's hashcode.
   */
  @Override public int hashCode() {
    return lhs.hashCode();
  }
  @Override public abstract String toString();
  public int getIndex() {
    return index;
  }
  @Override public int compareTo(final Rule other) {
    return Integer.compare(index, other.index);
  }
  public abstract List<Symbol> getChildren();
}