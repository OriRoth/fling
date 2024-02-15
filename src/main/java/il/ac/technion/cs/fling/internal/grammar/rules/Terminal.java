package il.ac.technion.cs.fling.internal.grammar.rules;
import il.ac.technion.cs.fling.internal.grammar.types.ClassParameter;
import il.ac.technion.cs.fling.internal.grammar.types.VarargsClassParameter;
import il.ac.technion.cs.fling.internal.grammar.types.VarargsVariableTypeParameter;
import il.ac.technion.cs.fling.internal.grammar.types.VariableTypeParameter;

import java.util.Arrays;

/** Language terminal symbol, never occurs in grammar;
 *
 * @see Token
 * @author Ori Roth */
public interface Terminal extends TempSymbol {
  @Override default Token normalize() {
    return new Token(this);
  }
  /** Associate parameters with this terminal
   *
   * @param types parameter types
   * @return newly created token */
  default Token with(final Class<?>... types) {
    return new Token(this, Arrays.stream(types).map(ClassParameter::new).toArray(ClassParameter[]::new));
  }
  /** Assign parameter varargs to this terminal.
   *
   * @param parameterClass parameter type
   * @return newly created token */
  default Token many(final Class<?> parameterClass) {
    return new Token(this, new VarargsClassParameter(parameterClass));
  }
  /** Assign variables as parameter to this terminal.
   *
   * @param variables parameter variables
   * @return newly created token */
  default Token with(final Variable... variables) {
    return new Token(this, Arrays.stream(variables).map(VariableTypeParameter::new).toArray(VariableTypeParameter[]::new));
  }
  /** Assign variable as varargs parameter to this terminal.
   *
   * @param variable parameter variable
   * @return newly created token */
  default Token many(final Variable variable) {
    return new Token(this, new VarargsVariableTypeParameter(variable));
  }
  static Terminal byName(final String name) {
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
