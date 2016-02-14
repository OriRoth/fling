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
    this(rule, lookahead, 0, label,false);
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
    return rule.hashCode() + Integer.hashCode(dotIndex);
  }
  @Override public boolean equals(final Object obj) {
    if(obj == null)
      return false;
    if (obj.getClass() != getClass())
      return false;
    JItem i = (JItem) obj;
    return dotIndex == i.dotIndex && rule.equals(i.rule) && lookahead.equals(i.lookahead);
  }
  public boolean readyToReduce() {
    return dotIndex == rule.getChildren().size();
  }
   public JItem advance() {
    if (dotIndex == rule.getChildren().size())
      throw new IllegalStateException("cannot advance a ready to reduce item");
    return new JItem(rule, lookahead, dotIndex + 1, label, false);
  }
  public JItem asKernel(){
    return new JItem(rule, lookahead,dotIndex, label, true);
  }
}
