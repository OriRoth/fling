package fling.grammar.sententials;

import fling.grammar.types.ClassParameter;
import fling.grammar.types.VarargsClassParameter;
import fling.grammar.types.VarargsVariableTypeParameter;
import fling.grammar.types.VariableTypeParameter;

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
