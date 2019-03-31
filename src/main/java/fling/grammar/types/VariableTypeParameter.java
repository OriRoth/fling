package fling.grammar.types;

import java.util.Collections;
import java.util.Set;

import fling.grammar.sententials.Variable;

public class VariableTypeParameter implements TypeParameter {
  public final Variable variable;

  public VariableTypeParameter(Variable variable) {
    this.variable = variable;
  }
  @Override public String baseParameterName() {
    return variable.name();
  }
  @Override public Set<Variable> declaredHeadVariables() {
    return Collections.singleton(variable);
  }
  @Override public String toString() {
    return variable.name();
  }
}
