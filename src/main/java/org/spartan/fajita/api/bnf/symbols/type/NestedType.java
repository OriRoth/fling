package org.spartan.fajita.api.bnf.symbols.type;

import org.spartan.fajita.api.bnf.symbols.NonTerminal;
import org.spartan.fajita.api.export.ASTNode;

public class NestedType implements ParameterType {
  public final NonTerminal nested;

  public NestedType(NonTerminal nested) {
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
    if (nested == null) {
      if (other.nested != null)
        return false;
    } else if (!nested.equals(other.nested))
      return false;
    return true;
  }
  @Override public boolean accepts(Object... args) {
    return args.length == 1 && nested.equals(args[0]) || ASTNode.class.isInstance(args[0]);
  }
}
