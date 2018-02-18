package org.spartan.fajita.revision.symbols.types;

public interface ParameterType {
  boolean accepts(Object arg);
  default String toParameterString() {
    return toString();
  }
}
