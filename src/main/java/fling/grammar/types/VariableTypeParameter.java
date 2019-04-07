package fling.grammar.types;

import java.util.Collections;
import java.util.Set;

import fling.grammar.sententials.Variable;
import fling.namers.NaiveNamer;

public class VariableTypeParameter implements TypeParameter {
  public final Variable variable;

  public VariableTypeParameter(Variable variable) {
    this.variable = variable;
  }
  @Override public String baseParameterName() {
    return NaiveNamer.lowerCamelCase(variable.name());
  }
  @Override public Set<Variable> declaredHeadVariables() {
    return Collections.singleton(variable);
  }
  @Override public int hashCode() {
    return variable.hashCode();
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof VariableTypeParameter))
      return false;
    VariableTypeParameter other = (VariableTypeParameter) obj;
    return variable.equals(other.variable);
  }
  @Override public String toString() {
    return variable.name();
  }
}
