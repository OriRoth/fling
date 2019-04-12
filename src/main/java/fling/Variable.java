package fling;

import fling.internal.grammar.sententials.Symbol;

public interface Variable extends Symbol {
  static Variable byName(final String name) {
    return new Variable() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public boolean equals(final Object obj) {
        if (this == obj)
          return true;
        if (!(obj instanceof Variable))
          return false;
        final Variable other = (Variable) obj;
        return name().equals(other.name());
      }
    };
  }
}
