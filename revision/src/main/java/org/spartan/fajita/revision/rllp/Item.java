package org.spartan.fajita.revision.rllp;

import java.util.List;

import org.spartan.fajita.revision.bnf.DerivationRule;
import org.spartan.fajita.revision.symbols.Symbol;

public class Item {
  public final DerivationRule rule;
  public final int dotIndex;

  public Item(DerivationRule rule, int dotIndex) {
    this.dotIndex = dotIndex;
    this.rule = rule;
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(rule.lhs + " ::= ");
    List<Symbol> expression = rule.rhs;
    for (int i = 0; i < expression.size(); i++) {
      if (i == dotIndex)
        sb.append("\u00b7");
      Symbol symb = expression.get(i);
      sb.append(symb.toString() + " ");
    }
    if (expression.size() == dotIndex)
      sb.append("\u00b7");
    return sb.toString();
  }
  public Symbol afterDot() {
    return rule.get(dotIndex);
  }
  public Symbol beforeDot() {
    return rule.get(dotIndex - 1);
  }
  public boolean isLegalTransition(final Symbol s) {
    return ((rule.size() > dotIndex) && s.equals(rule.get(dotIndex)));
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dotIndex;
    result = prime * result + rule.hashCode();
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Item other = (Item) obj;
    if (dotIndex != other.dotIndex)
      return false;
    return rule.equals(other.rule);
  }
  public boolean readyToReduce() {
    return dotIndex == rule.size();
  }
  public Item advance() {
    if (dotIndex == rule.size())
      throw new IllegalStateException("Cannot advance a ready to reduce item");
    return new Item(rule, dotIndex + 1);
  }
}
