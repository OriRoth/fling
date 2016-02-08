package org.spartan.fajita.api.parser.stack;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.parser.old.Item;

public class JItem extends Item {

  public final int label;

  public JItem(DerivationRule rule, Verb lookahead, int dotIndex , int address) {
    super(rule, lookahead, dotIndex);
    this.label = address;
  }
  
  @Override public String toString() {
    return super.toString() + ", L"+label+"";
  }

  @Override public int hashCode() {
    return super.hashCode();
  }

  @Override public boolean equals(Object obj) {
    return super.equals(obj);
  }
  
  @Override public JItem advance() {
    if (dotIndex == rule.getChildren().size())
      throw new IllegalStateException("cannot advance a ready to reduce item");
    return new JItem(rule, lookahead, dotIndex + 1,label);
  }
  
}