package org.spartan.fajita.revision.symbols.types;

import java.lang.reflect.Array;

public class VarArgs implements ParameterType {
  public final Class<?> aclazz;
  public final Class<?> clazz;

  public <T> VarArgs(Class<T> clazz) {
    this.aclazz = Array.newInstance(clazz, 0).getClass();
    this.clazz = clazz;
  }
  @Override public String toString() {
    return aclazz.getTypeName();
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((aclazz == null) ? 0 : aclazz.hashCode());
    return result;
  }
  @Override public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof VarArgs))
      return false;
    VarArgs other = (VarArgs) obj;
    return aclazz.equals(other.aclazz);
  }
  @Override public boolean accepts(Object arg) {
    return clazz.isInstance(arg);
  }
}
