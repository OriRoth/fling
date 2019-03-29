package fling.grammar.sententials;

import fling.grammar.types.ClassParameter;

public interface Terminal extends Symbol {
  default Verb with(Class<?> parameterClass) {
    return new Verb(this, new ClassParameter(parameterClass));
  }
}
