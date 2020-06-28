package il.ac.technion.cs.fling.internal.grammar.types;
import java.util.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
import il.ac.technion.cs.fling.namers.NaiveLinker;
public class VarargsVariableTypeParameter implements Parameter {
  public final Variable variable;
  public VarargsVariableTypeParameter(final Variable variable) {
    this.variable = variable;
  }
  @Override public String baseParameterName() {
    return NaiveLinker.lowerCamelCase(variable.name() + "s");
  }
  @Override public Set<Variable> declaredHeadVariables() {
    return Collections.singleton(variable);
  }
  @Override public int hashCode() {
    return Objects.hash(variable);
  }
  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null)
      return false;
    if (getClass() != o.getClass())
      return false;
    final VarargsVariableTypeParameter other = (VarargsVariableTypeParameter) o;
    return Objects.equals(variable, other.variable);
  }
  @Override public String toString() {
    return variable.name() + "...";
  }
}
