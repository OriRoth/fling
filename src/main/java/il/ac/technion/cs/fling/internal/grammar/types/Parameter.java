package il.ac.technion.cs.fling.internal.grammar.types;
import java.util.Collections;
import java.util.Set;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public interface Parameter {
  String baseParameterName();
  default Set<Variable> declaredHeadVariables() {
    return Collections.emptySet();
  }
  default boolean isStringTypeParameter() {
    return this instanceof ClassTypeParameter;
  }
  default boolean isVariableTypeParameter() {
    return this instanceof VariableTypeParameter;
  }
  default boolean isVarargsTypeParameter() {
    return this instanceof VarargsVariableTypeParameter;
  }
  default ClassTypeParameter asStringTypeParameter() {
    return (ClassTypeParameter) this;
  }
  default VariableTypeParameter asVariableTypeParameter() {
    return (VariableTypeParameter) this;
  }
  default VarargsVariableTypeParameter asVarargsVariableTypeParameter() {
    return (VarargsVariableTypeParameter) this;
  }
  static Parameter of(final Class<?> c) {
    return new ClassParameter(c);
  }
}
