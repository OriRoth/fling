package org.spartan.fajita.revision.symbols.types;

import java.util.HashMap;
import java.util.Map;

public class ClassType implements ParameterType {
  public final Class<?> clazz;
  private static final Map<Class<?>, Class<?>> boxedClasses = new HashMap<>();
  {
    boxedClasses.put(int.class, Integer.class);
    boxedClasses.put(long.class, Long.class);
    boxedClasses.put(short.class, Short.class);
    boxedClasses.put(char.class, Character.class);
    boxedClasses.put(boolean.class, Boolean.class);
    boxedClasses.put(byte.class, Byte.class);
    boxedClasses.put(float.class, Float.class);
    boxedClasses.put(double.class, Double.class);
    boxedClasses.put(void.class, Void.class); // Oh well... still illegal though
  }

  public ClassType(final Class<?> clazz) {
    if (void.class.equals(clazz) || Void.class.equals(clazz))
      throw new RuntimeException("Cannot use void class as attribute");
    this.clazz = boxedClasses.getOrDefault(clazz, clazz);
  }
  @Override public String toString() {
    return clazz.getName();
  }
  @Override public int hashCode() {
    final int prime = 17;
    int result = 1;
    result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof ClassType))
      return false;
    return clazz.equals(((ClassType) obj).clazz);
  }
  @Override public boolean accepts(Object arg) {
    return clazz.isInstance(arg);
  }
}
