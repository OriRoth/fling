package org.spartan.fajita.api.bnf.symbols.type;

import java.lang.reflect.Array;

public class VarArgs implements ParameterType {
  public final Class<?> clazz;

  public <T> VarArgs(Class<T> clazz) {
    this.clazz = Array.newInstance(clazz, 0).getClass();
  }
  @Override public String toString() {
    return clazz.getTypeName();
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
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
    if (clazz == null) {
      if (other.clazz != null)
        return false;
    } else if (!clazz.equals(other.clazz))
      return false;
    return true;
  }
  @Override public boolean accepts(Object... args) {
    return args.length == 0 || clazz.isInstance(args[0]);
  }
}
