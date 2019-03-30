package fling.grammar.types;

import java.util.Objects;

public class VarargsClassParameter implements TypeParameter {
  public final Class<?> parameterClass;

  public VarargsClassParameter(Class<?> parameterClass) {
    this.parameterClass = Objects.requireNonNull(parameterClass);
  }
  @Override public String typeName() {
    return parameterClass.getCanonicalName() + "[]";
  }
  @Override public String parameterTypeName() {
    return parameterClass.getCanonicalName() + "...";
  }
  @Override public String baseParameterName() {
    return String.valueOf(Character.toLowerCase(parameterClass.getName().charAt(0)));
  }
  @Override public int hashCode() {
    return parameterClass.hashCode();
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof VarargsClassParameter))
      return false;
    VarargsClassParameter other = (VarargsClassParameter) obj;
    return parameterClass.equals(other.parameterClass);
  }
  @Override public String toString() {
    return parameterClass.getCanonicalName() + "...";
  }
}
