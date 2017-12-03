package org.spartan.fajita.revision.symbols.types;

public class ClassType implements ParameterType {
  public final Class<?> clazz;

  public ClassType(final Class<?> clazz) {
    this.clazz = clazz;
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
