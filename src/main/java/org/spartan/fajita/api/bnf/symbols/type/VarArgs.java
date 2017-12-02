package org.spartan.fajita.api.bnf.symbols.type;

import java.lang.reflect.Array;
import java.util.Arrays;

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
    if (getClass() != obj.getClass())
      return false;
    VarArgs other = (VarArgs) obj;
    if (aclazz == null) {
      if (other.aclazz != null)
        return false;
    } else if (!aclazz.equals(other.aclazz))
      return false;
    return true;
  }
  @Override public boolean accepts(Object[] args) {
    return Arrays.stream(args).allMatch(x -> clazz.isInstance(x));
  }
}
