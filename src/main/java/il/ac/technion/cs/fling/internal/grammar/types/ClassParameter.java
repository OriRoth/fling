package il.ac.technion.cs.fling.internal.grammar.types;
import static java.util.Objects.requireNonNull;
import java.util.Objects;
import il.ac.technion.cs.fling.internal.compiler.Linker;
// TODO allow primitive types.
public class ClassParameter implements ClassTypeParameter {
  private final Class<?> parameterClass;
  public ClassParameter(final Class<?> parameterClass) {
    this.parameterClass = requireNonNull(parameterClass);
  }
  @Override public String typeName() {
    return parameterClass.getCanonicalName();
  }
  @Override public String baseParameterName() {
    return unPrimitiveTypeSimple(Linker.lowerCamelCase(parameterClass.getSimpleName()));
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
    final ClassParameter other = (ClassParameter) o;
    return Objects.equals(parameterClass, other.parameterClass);
  }
  @Override public String toString() {
    return parameterClass.getSimpleName();
  }
  public static String unPrimitiveType(final String typeName) {
    return byte.class.getName().equals(typeName) ? Byte.class.getCanonicalName() : //
        short.class.getName().equals(typeName) ? Short.class.getCanonicalName() : //
            int.class.getName().equals(typeName) ? Integer.class.getCanonicalName() : //
                long.class.getName().equals(typeName) ? Long.class.getCanonicalName() : //
                    float.class.getName().equals(typeName) ? Float.class.getCanonicalName() : //
                        double.class.getName().equals(typeName) ? Double.class.getCanonicalName() : //
                            boolean.class.getName().equals(typeName) ? Boolean.class.getCanonicalName() : //
                                char.class.getName().equals(typeName) ? Character.class.getCanonicalName() : //
                                    void.class.getName().equals(typeName) ? Void.class.getCanonicalName() : //
                                        typeName;
  }
  private static String unPrimitiveTypeSimple(final String typeName) {
    return byte.class.getName().equals(typeName) ? "b" : //
        short.class.getName().equals(typeName) ? "s" : //
            int.class.getName().equals(typeName) ? "i" : //
                long.class.getName().equals(typeName) ? "l" : //
                    float.class.getName().equals(typeName) ? "f" : //
                        double.class.getName().equals(typeName) ? "d" : //
                            boolean.class.getName().equals(typeName) ? "b" : //
                                char.class.getName().equals(typeName) ? "c" : //
                                    void.class.getName().equals(typeName) ? "v" : //
                                        typeName;
  }
}
