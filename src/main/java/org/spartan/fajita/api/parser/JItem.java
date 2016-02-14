package org.spartan.fajita.api.parser;

import java.util.List;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.Symbol;
import org.spartan.fajita.api.bnf.symbols.Verb;

public class JItem {
  public final DerivationRule rule;
  public final int dotIndex;
  public final Verb lookahead;
  public final int label;
  public final boolean kernel;

  private JItem(DerivationRule rule, Verb lookahead, int dotIndex, int label, boolean isKernel) {
    this.lookahead = lookahead;
    this.dotIndex = dotIndex;
    this.rule = rule;
    this.label = label;
    this.kernel = isKernel;
  }
  public JItem(DerivationRule rule, Verb lookahead, int label) {
    this(rule, lookahead, 0, label, false);
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder(rule.lhs.serialize() + " ::= ");
    List<Symbol> expression = rule.getChildren();
    for (int i = 0; i < expression.size(); i++) {
      if (i == dotIndex)
        sb.append(". ");
      Symbol symb = expression.get(i);
      sb.append(symb.toString() + " ");
    }
    if (expression.size() == dotIndex)
      sb.append(". ");
    sb.append("," + lookahead.toString());
    sb.append(", L" + label + (kernel ? ",K" : ""));
    return sb.toString();
  }
  public boolean isLegalTransition(final Symbol symb) {
    return ((rule.getChildren().size() > dotIndex) //
        && symb.equals(rule.getChildren().get(dotIndex)));
  }
  public boolean isLegalReduce(final Verb term) {
    return readyToReduce() && lookahead.equals(term);
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dotIndex;
    result = prime * result + ((lookahead == null) ? 0 : lookahead.hashCode());
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
    JItem other = (JItem) obj;
    if (dotIndex != other.dotIndex)
      return false;
    if (lookahead == null) {
      if (other.lookahead != null)
        return false;
    } else if (!lookahead.equals(other.lookahead))
      return false;
    if (rule == null) {
      if (other.rule != null)
        return false;
    } else if (!rule.equals(other.rule))
      return false;
    return true;
  }
  public boolean strongEquals(JItem other) {
    return equals(other) && label == other.label;
  }
  public boolean readyToReduce() {
    return dotIndex == rule.getChildren().size();
  }
  public JItem advance() {
    if (dotIndex == rule.getChildren().size())
      throw new IllegalStateException("cannot advance a ready to reduce item");
    return new JItem(rule, lookahead, dotIndex + 1, label, false);
  }
  public JItem asKernel() {
    return new JItem(rule, lookahead, dotIndex, label, true);
  }
}
