package il.ac.technion.cs.fling.internal.grammar.types;

import java.util.Objects;

import il.ac.technion.cs.fling.namers.NaiveNamer;

public class VarargsClassParameter implements StringTypeParameter {
  public final Class<?> parameterClass;

  public VarargsClassParameter(final Class<?> parameterClass) {
    this.parameterClass = Objects.requireNonNull(parameterClass);
  }
  @Override public String typeName() {
    return parameterClass.getCanonicalName() + "[]";
  }
  @Override public String parameterTypeName() {
    return parameterClass.getCanonicalName() + "...";
  }
  @Override public String baseParameterName() {
    return NaiveNamer.lowerCamelCase(parameterClass.getSimpleName() + "s");
  }
  @Override public int hashCode() {
    return parameterClass.hashCode();
  }
  @Override public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof VarargsClassParameter))
      return false;
    final VarargsClassParameter other = (VarargsClassParameter) obj;
    return parameterClass.equals(other.parameterClass);
  }
  @Override public String toString() {
    return parameterClass.getCanonicalName() + "...";
  }
}
