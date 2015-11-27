package org.spartan.fajita.api.ast;

import org.spartan.fajita.api.bnf.symbols.SpecialSymbols;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public abstract class AbstractNode {
  
  protected Compound parent;

  public abstract Symbol getSymbol();
  protected void setParent(Compound parent) {
    this.parent = parent;
  }
  public Compound getParent() {
    return parent;
  }
  @Override public String toString() {
    return getSymbol().name().toString() + " : " + this.getClass().getSimpleName();
  }
  public Compound getRoot() {
    Compound current = this.getClass()==Compound.class ? (Compound)this:this.parent;
    while(current.parent != null)
      current = current.parent;
    return current;
  }
  
  public static final AbstractNode epsilon = new AbstractNode(){
    @Override public Symbol getSymbol() {
      return SpecialSymbols.epsilon;
    }
  };
}
