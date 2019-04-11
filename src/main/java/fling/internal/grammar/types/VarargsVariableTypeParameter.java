package fling.internal.grammar.types;

import java.util.Collections;
import java.util.Set;

import fling.internal.grammar.sententials.Variable;
import fling.namers.NaiveNamer;

public class VarargsVariableTypeParameter implements TypeParameter {
  public final Variable variable;

  public VarargsVariableTypeParameter(Variable variable) {
    this.variable = variable;
  }
  @Override public String baseParameterName() {
    return NaiveNamer.lowerCamelCase(variable.name() + "s");
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
    if (!(obj instanceof VarargsVariableTypeParameter))
      return false;
    VarargsVariableTypeParameter other = (VarargsVariableTypeParameter) obj;
    return variable.equals(other.variable);
  }
  @Override public String toString() {
    return variable.name() + "...";
  }
}
