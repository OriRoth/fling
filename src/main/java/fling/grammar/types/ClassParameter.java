package fling.grammar.types;

import static java.util.Objects.requireNonNull;

// TODO allow primitive types.
public class ClassParameter implements StringTypeParameter {
  public final Class<?> parameterClass;

  public ClassParameter(Class<?> parameterClass) {
    this.parameterClass = replacePrimitive(requireNonNull(parameterClass));
  }
  private static Class<?> replacePrimitive(Class<?> c) {
    return byte.class.equals(c) ? Byte.class : //
        short.class.equals(c) ? Short.class : //
            int.class.equals(c) ? Integer.class : //
                long.class.equals(c) ? Long.class : //
                    float.class.equals(c) ? Float.class : //
                        double.class.equals(c) ? Double.class : //
                            boolean.class.equals(c) ? Boolean.class : //
                                char.class.equals(c) ? Character.class : //
                                    void.class.equals(c) ? Void.class : //
                                        c;
  }
  @Override public String typeName() {
    return parameterClass.getCanonicalName();
  }
  @Override public String baseParameterName() {
    return String.valueOf(Character.toLowerCase(parameterClass.getSimpleName().charAt(0)));
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
}
