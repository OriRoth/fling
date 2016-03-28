package org.spartan.fajita.api.rllp;

import java.util.List;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class Item {
  public final DerivationRule rule;
  public final int dotIndex;

  public Item(DerivationRule rule, int dotIndex) {
    this.dotIndex = dotIndex;
    this.rule = rule;
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(rule.lhs.toString2() + " ::= ");
    List<Symbol> expression = rule.getChildren();
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
  public Symbol afterDot(){
    return rule.getChildren().get(dotIndex);
  }
  public boolean isLegalTransition(final Symbol symb) {
    return ((rule.getChildren().size() > dotIndex) //
        && symb.equals(rule.getChildren().get(dotIndex)));
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dotIndex;
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
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
    if (rule == null) {
      if (other.rule != null)
        return false;
    } else if (!rule.equals(other.rule))
      return false;
    return true;
  }
  public boolean readyToReduce() {
    return dotIndex == rule.getChildren().size();
  }
  public Item advance() {
    if (dotIndex == rule.getChildren().size())
      throw new IllegalStateException("cannot advance a ready to reduce item");
    return new Item(rule, dotIndex + 1);
  }
}
