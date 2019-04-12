package fling.internal.grammar.sententials;

import fling.internal.grammar.types.*;

public interface Terminal extends Symbol {
  default Verb with(final Class<?> parameterClass) {
    return new Verb(this, new ClassParameter(parameterClass));
  }
  default Verb many(final Class<?> parameterClass) {
    return new Verb(this, new VarargsClassParameter(parameterClass));
  }
  default Verb with(final Variable variable) {
    return new Verb(this, new VariableTypeParameter(variable));
  }
  default Verb many(final Variable variable) {
    return new Verb(this, new VarargsVariableTypeParameter(variable));
  }
}
