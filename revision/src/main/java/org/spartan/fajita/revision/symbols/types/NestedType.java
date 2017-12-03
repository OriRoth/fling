package org.spartan.fajita.revision.symbols.types;

import org.spartan.fajita.revision.export.ASTNode;
import org.spartan.fajita.revision.symbols.Extendible;
import org.spartan.fajita.revision.symbols.NonTerminal;
import org.spartan.fajita.revision.symbols.Symbol;

public class NestedType implements ParameterType {
  public final Symbol nested;

  public NestedType(NonTerminal nested) {
    this.nested = nested;
  }
  public NestedType(Extendible nested) {
    this.nested = nested;
  }
  public NestedType(Symbol nested) {
    assert nested.isExtendible() || nested.isNonTerminal();
    this.nested = nested;
  }
  @Override public String toString() {
    return nested.name();
  }
  public String toString(String packagePath, String topClassName) {
    return packagePath + "." + topClassName + "." + nested.name();
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nested == null) ? 0 : nested.hashCode());
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NestedType other = (NestedType) obj;
    return nested.equals(other.nested);
  }
  @Override public boolean accepts(Object arg) {
    return nested.equals(arg) || ASTNode.class.isInstance(arg);
  }
}
