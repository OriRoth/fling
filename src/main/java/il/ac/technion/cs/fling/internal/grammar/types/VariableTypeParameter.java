package il.ac.technion.cs.fling.internal.grammar.types;
import java.util.*;
import il.ac.technion.cs.fling.internal.compiler.Linker;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public class VariableTypeParameter implements Parameter {
  public final Variable variable;
  public VariableTypeParameter(final Variable variable) {
    this.variable = variable;
  }
  @Override public String baseParameterName() {
    return Linker.lowerCamelCase(variable.name());
  }
  @Override public Set<Variable> declaredHeadVariables() {
    return Collections.singleton(variable);
  }
  @Override public int hashCode() {
    return Objects.hash(variable);
  }
  @Override public boolean equals(final Object that) {
    if (that == this)
      return true;
    if (that == null)
      return false;
    if (getClass() != that.getClass())
      return false;
    return equals((VariableTypeParameter) that);
  }
  private boolean equals(final VariableTypeParameter that) {
    return Objects.equals(variable, that.variable);
  }
  @Override public String toString() {
    return variable.name();
  }
}
