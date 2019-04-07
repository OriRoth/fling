package fling.grammar.types;

import static java.util.Objects.requireNonNull;

import fling.namers.NaiveNamer;

// TODO allow primitive types.
public class ClassParameter implements StringTypeParameter {
  public final Class<?> parameterClass;

  public ClassParameter(Class<?> parameterClass) {
    this.parameterClass = requireNonNull(parameterClass);
  }
  @Override public String typeName() {
    return parameterClass.getCanonicalName();
  }
  @Override public String baseParameterName() {
    return NaiveNamer.lowerCamelCase(unPrimitiveTypeSimple(parameterClass.getSimpleName()));
  }
  @Override public int hashCode() {
    return parameterClass.hashCode();
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ClassParameter))
      return false;
    ClassParameter other = (ClassParameter) obj;
    return parameterClass.equals(other.parameterClass);
  }
  @Override public String toString() {
    return parameterClass.getCanonicalName();
  }
  public static String unPrimitiveType(String typeName) {
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
  public static String unPrimitiveTypeSimple(String typeName) {
    return byte.class.getName().equals(typeName) ? Byte.class.getSimpleName() : //
        short.class.getName().equals(typeName) ? Short.class.getSimpleName() : //
            int.class.getName().equals(typeName) ? Integer.class.getSimpleName() : //
                long.class.getName().equals(typeName) ? Long.class.getSimpleName() : //
                    float.class.getName().equals(typeName) ? Float.class.getSimpleName() : //
                        double.class.getName().equals(typeName) ? Double.class.getSimpleName() : //
                            boolean.class.getName().equals(typeName) ? Boolean.class.getSimpleName() : //
                                char.class.getName().equals(typeName) ? Character.class.getSimpleName() : //
                                    void.class.getName().equals(typeName) ? Void.class.getSimpleName() : //
                                        typeName;
  }
}
