package il.ac.technion.cs.fling.internal.grammar.types;

import static java.util.Objects.requireNonNull;

import il.ac.technion.cs.fling.namers.NaiveNamer;

// TODO allow primitive types.
public class ClassParameter implements StringTypeParameter {
  public final Class<?> parameterClass;

  public ClassParameter(final Class<?> parameterClass) {
    this.parameterClass = requireNonNull(parameterClass);
  }

  @Override public String typeName() {
    return parameterClass.getCanonicalName();
  }

  @Override public String baseParameterName() {
    return unPrimitiveTypeSimple(NaiveNamer.lowerCamelCase(parameterClass.getSimpleName()));
  }

  @Override public int hashCode() {
    return parameterClass.hashCode();
  }

  @Override public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ClassParameter))
      return false;
    final ClassParameter other = (ClassParameter) obj;
    return parameterClass.equals(other.parameterClass);
  }

  @Override public String toString() {
    return parameterClass.getCanonicalName();
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

  public static String unPrimitiveTypeSimple(final String typeName) {
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
