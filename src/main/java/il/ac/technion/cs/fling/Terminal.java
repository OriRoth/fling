package il.ac.technion.cs.fling;

import il.ac.technion.cs.fling.internal.grammar.sententials.Token;
import il.ac.technion.cs.fling.internal.grammar.types.*;

/** Language terminal symbol---atom.
 * 
 * @author Ori Roth */
public interface Terminal extends SymbolX {
  @Override default Token normalize() {
    return new Token(this);
  }

  /** Associate parameter with this terminal
   * 
   * @param clazz parameter type
   * @return newly created token */
  default Token with(final Class<?> clazz) {
    return new Token(this, new ClassParameter(clazz));
  }

  /** Assign parameter varargs to this terminal.
   * 
   * @param parameterClass parameter type
   * @return newly created token */
  default Token many(final Class<?> parameterClass) {
    return new Token(this, new VarargsClassParameter(parameterClass));
  }

  /** Assign variable as parameter to this terminal.
   * 
   * @param variable parameter variable
   * @return newly created token */
  default Token with(final Variable variable) {
    return new Token(this, new VariableTypeParameter(variable));
  }

  /** Assign variable as varargs parameter to this terminal.
   * 
   * @param variable parameter variable
   * @return newly created token */
  default Token many(final Variable variable) {
    return new Token(this, new VarargsVariableTypeParameter(variable));
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
