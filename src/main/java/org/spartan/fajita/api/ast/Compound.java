package org.spartan.fajita.api.ast;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Compound implements Iterable<Compound> {
  protected final ArrayList<Compound> children;
  public final String name;
  private Compound parent;

  public Compound(final Compound parent) {
    setParent(parent);
    name = getName();
    children = getChildren();
  }
  protected abstract ArrayList<Compound> getChildren();
  public abstract String getName();
  public Compound getChild(final int index) {
    return children.get(index);
  }
  @Override public String toString() {
    return name.toString() + " : " + this.getClass().getSimpleName();
  }
  public Compound getRoot() {
    Compound current = this;
    for (; current.getParent() != null; current = current.getParent())
      ;
    return current;
  }
  public Compound getParent() {
    return parent;
  }
  protected void setParent(final Compound parent) {
    this.parent = parent;
  }
  @Override public Iterator<Compound> iterator() {
    return new Iterator<Compound>() {
      private final int size;
      private int current;

      {
        size = children.size();
        current = 0;
      }
      @Override public boolean hasNext() {
        return current < size;
      }
      @Override public Compound next() {
        return getChild(current++);
      }
    };
  }
}