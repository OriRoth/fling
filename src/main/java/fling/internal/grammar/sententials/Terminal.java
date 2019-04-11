package fling.internal.grammar.sententials;

import fling.internal.grammar.types.ClassParameter;
import fling.internal.grammar.types.VarargsClassParameter;
import fling.internal.grammar.types.VarargsVariableTypeParameter;
import fling.internal.grammar.types.VariableTypeParameter;

public interface Terminal extends Symbol {
  default Verb with(Class<?> parameterClass) {
    return new Verb(this, new ClassParameter(parameterClass));
  }
  default Verb many(Class<?> parameterClass) {
    return new Verb(this, new VarargsClassParameter(parameterClass));
  }
  default Verb with(Variable variable) {
    return new Verb(this, new VariableTypeParameter(variable));
  }
  default Verb many(Variable variable) {
    return new Verb(this, new VarargsVariableTypeParameter(variable));
  }
}
