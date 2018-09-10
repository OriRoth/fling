package roth.ori.fling.symbols.types;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import roth.ori.fling.export.ASTNode;
import roth.ori.fling.parser.ell.Interpretation;
import roth.ori.fling.symbols.NonTerminal;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.extendibles.Extendible;

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
    return nested.toString();
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
  @SuppressWarnings("rawtypes") @Override public List conclude(Object arg, BiFunction<Symbol, List, List> solution) {
    Interpretation i = (Interpretation) arg;
    return solution.apply(i.symbol, i.value);
  }
  @SuppressWarnings("rawtypes") @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return nested.toClasses(classSolution);
  }
}
