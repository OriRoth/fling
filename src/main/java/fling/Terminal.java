package fling;

import fling.internal.grammar.sententials.*;
import fling.internal.grammar.types.*;

/**
 * Language terminal symbol---atom.
 * 
 * @author Ori Roth
 */
public interface Terminal extends Symbol {
  /**
   * Assign parameter to this terminal.
   * 
   * @param parameterClass parameter type
   * @return corresponding verb
   */
  default Verb with(final Class<?> parameterClass) {
    return new Verb(this, new ClassParameter(parameterClass));
  }
  /**
   * Assign parameter varargs to this terminal.
   * 
   * @param parameterClass parameter type
   * @return corresponding verb
   */
  default Verb many(final Class<?> parameterClass) {
    return new Verb(this, new VarargsClassParameter(parameterClass));
  }
  /**
   * Assign variable as parameter to this terminal.
   * 
   * @param variable parameter variable
   * @return corresponding verb
   */
  default Verb with(final Variable variable) {
    return new Verb(this, new VariableTypeParameter(variable));
  }
  /**
   * Assign variable as varargs parameter to this terminal.
   * 
   * @param variable parameter variable
   * @return corresponding verb
   */
  default Verb many(final Variable variable) {
    return new Verb(this, new VarargsVariableTypeParameter(variable));
  }
}
