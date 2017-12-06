package org.spartan.fajita.revision.symbols.types;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.spartan.fajita.revision.parser.ell.Interpretation;
import org.spartan.fajita.revision.symbols.Symbol;

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
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List conclude(Object arg,
      BiFunction<Symbol, List, List> solution) {
    List l = (List) ((List) arg).stream()
        .map(x -> !(x instanceof Interpretation) ? x : solution.apply(((Interpretation) x).symbol, ((Interpretation) x).value))
        .collect(Collectors.toList());
    Object[] $ = (Object[]) Array.newInstance(clazz, l.size());
    for (int i = 0; i < $.length; ++i)
      $[i] = l.get(i);
    return Collections.singletonList($);
  }
  // NOTE the returned classes should be duplicated according to input size
  @SuppressWarnings({ "rawtypes", "unused" }) @Override public List<Class> toClasses(Function<Symbol, Class> classSolution) {
    return Collections.singletonList(clazz);
  }
}
