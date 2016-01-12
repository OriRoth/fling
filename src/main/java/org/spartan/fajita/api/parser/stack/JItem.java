package org.spartan.fajita.api.parser.stack;

import org.spartan.fajita.api.bnf.rules.DerivationRule;
import org.spartan.fajita.api.bnf.symbols.Verb;
import org.spartan.fajita.api.parser.old.Item;

public class JItem extends Item {

  public final int address;

  public JItem(DerivationRule rule, Verb lookahead, int dotIndex , int address) {
    super(rule, lookahead, dotIndex);
    this.address = address;
  }
  
  @Override public String toString() {
    return super.toString() + ", Addr("+address+")";
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + address;
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    JItem other = (JItem) obj;
    if (address != other.address)
      return false;
    return true;
  }
  
  @Override public JItem advance() {
    if (dotIndex == rule.getChildren().size())
      throw new IllegalStateException("cannot advance a ready to reduce item");
    return new JItem(rule, lookahead, dotIndex + 1,address);
  }
  
}
