package fling.grammar.types;

import java.util.Collections;
import java.util.Set;

import fling.grammar.sententials.Variable;

public interface TypeParameter {
  String baseParameterName();
  default Set<Variable> declaredHeadVariables() {
    return Collections.emptySet();
  }
  default boolean isStringTypeParameter() {
    return this instanceof StringTypeParameter;
  }
  default boolean isVariableTypeParameter() {
    return this instanceof VariableTypeParameter;
  }
  default StringTypeParameter asStringTypeParameter() {
    return (StringTypeParameter) this;
  }
  default VariableTypeParameter asVariableTypeParameter() {
    return (VariableTypeParameter) this;
  }
}
