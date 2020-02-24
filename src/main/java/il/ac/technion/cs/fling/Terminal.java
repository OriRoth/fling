package il.ac.technion.cs.fling;

import il.ac.technion.cs.fling.internal.grammar.sententials.Verb;
import il.ac.technion.cs.fling.internal.grammar.types.*;

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
  public static Terminal byName(final String name) {
    return new Terminal() {
      @Override public String name() {
        return name;
      }
      @Override public String toString() {
        return name;
      }
      @Override public int hashCode() {
        return name.hashCode();
      }
      @Override public boolean equals(final Object obj) {
        if (this == obj)
          return true;
        if (!(obj instanceof Terminal))
          return false;
        final Terminal other = (Terminal) obj;
        return name().equals(other.name());
      }
    };
  }
}
