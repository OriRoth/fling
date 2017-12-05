package org.spartan.fajita.revision.parser.ell;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.spartan.fajita.revision.symbols.Symbol;

public class ERuntimeItem {
  public final List<Symbol> rhs;
  public int dotIndex;
  public final List<Interpretation> interpretations;

  public ERuntimeItem(List<Symbol> rhs, int dotIndex) {
    this.dotIndex = dotIndex;
    this.rhs = rhs;
    this.interpretations = new LinkedList<>();
  }
  public Symbol afterDot() {
    return rhs.get(dotIndex);
  }
  public Symbol beforeDot() {
    return rhs.get(dotIndex - 1);
  }
  public boolean isLegalTransition(final Symbol s) {
    return ((rhs.size() > dotIndex) && s.equals(rhs.get(dotIndex)));
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + dotIndex;
    result = prime * result + rhs.hashCode();
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof ERuntimeItem))
      return false;
    ERuntimeItem other = (ERuntimeItem) obj;
    if (dotIndex != other.dotIndex)
      return false;
    return rhs.equals(other.rhs);
  }
  public boolean readyToReduce() {
    return dotIndex == rhs.size();
  }
  public void advance(Object value) {
    if (dotIndex == rhs.size())
      throw new IllegalStateException("Cannot advance a ready to reduce item");
    interpretations.add(new Interpretation(afterDot(), value));
    ++dotIndex;
  }

  public static class Interpretation extends AbstractMap.SimpleEntry<Symbol, Object> {
    private static final long serialVersionUID = 6948208130895284355L;

    public Interpretation(Symbol key, Object value) {
      super(key, value);
    }
    @Override public String toString() {
      return "(" + getKey().name() + "->"
          + (!getValue().getClass().isArray() ? getValue() : Arrays.deepToString((Object[]) getValue())) + ")";
    }
  }

  public static ERuntimeItem of(Symbol s) {
    List<Symbol> $ = new LinkedList<>();
    $.add(s);
    return new ERuntimeItem($, 0);
  }
}
