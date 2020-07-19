package il.ac.technion.cs.fling.internal.grammar.types;
import java.util.Objects;
import il.ac.technion.cs.fling.internal.compiler.Linker;
public class VarargsClassParameter implements ClassTypeParameter {
  private final Class<?> parameterClass;
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
    return Linker.lowerCamelCase(parameterClass.getSimpleName() + "s");
  }
  @Override public int hashCode() {
    return Objects.hash(parameterClass);
  }
  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null)
      return false;
    if (getClass() != o.getClass())
      return false;
    final var other = (VarargsClassParameter) o;
    return Objects.equals(parameterClass, other.parameterClass);
  }
  @Override public String toString() {
    return parameterClass.getCanonicalName() + "...";
  }
}
