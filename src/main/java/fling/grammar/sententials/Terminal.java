package fling.grammar.sententials;

import fling.grammar.types.ClassParameter;
import fling.grammar.types.VarargsClassParameter;

public interface Terminal extends Symbol {
  default Verb with(Class<?> parameterClass) {
    return new Verb(this, new ClassParameter(parameterClass));
  }
  default Verb many(Class<?> parameterClass) {
    return new Verb(this, new VarargsClassParameter(parameterClass));
  }
}
