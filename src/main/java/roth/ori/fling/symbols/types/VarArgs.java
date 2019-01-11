package roth.ori.fling.symbols.types;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import roth.ori.fling.parser.ell.Interpretation;
import roth.ori.fling.symbols.Symbol;

public class VarArgs implements ParameterType {
  public final Class<?> aclazz;
  public final Class<?> clazz;

  public <T> VarArgs(Class<T> clazz) {
    this.aclazz = Array.newInstance(clazz, 0).getClass();
    this.clazz = clazz;
  }
  public static <T> VarArgs varargs(Class<T> clazz) {
    return new VarArgs(clazz);
  }
  @Override public String toString() {
    return aclazz.getTypeName();
  }
  @Override public String toParameterString() {
    return clazz.getTypeName() + "...";
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
    List l = (List) ((List) arg).stream().map(x -> {
      // TODO Roth: check whether it make sense
      if (!(x instanceof Interpretation))
        return x;
      List $ = solution.apply(((Interpretation) x).symbol, ((Interpretation) x).value);
      assert $.size() == 1;
      return $.get(0);
    }).collect(Collectors.toList());
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
