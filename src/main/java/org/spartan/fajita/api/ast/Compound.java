package org.spartan.fajita.api.ast;

import java.util.Iterator;
import java.util.List;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.bnf.symbols.Symbol;

public class Compound extends AbstractNode implements Iterable<AbstractNode> {
  private final List<AbstractNode> children;
  public final NonTerminal nt;
  public Compound(final NonTerminal nt,List<AbstractNode> children) {
    this.nt = nt;
    this.children = children;
    children.forEach(child -> child.parent = this);
  }
  @Override public Symbol getSymbol() {
    return nt;
  }
  public List<AbstractNode> getChildren(){
    return children;
  }
  public AbstractNode child(final int index) {
    return children.get(index);
  }
  @Override public Iterator<AbstractNode> iterator() {
    return new Iterator<AbstractNode>() {
      private final int size;
      private int current;

      {
        size = getChildren().size();
        current = 0;
      }
      @Override public boolean hasNext() {
        return current < size;
      }
      @Override public AbstractNode next() {
        return child(current++);
      }
    };
  }

}