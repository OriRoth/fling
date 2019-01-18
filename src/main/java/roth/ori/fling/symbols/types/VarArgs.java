package roth.ori.fling.symbols.types;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import roth.ori.fling.export.ASTNode;
import roth.ori.fling.parser.ell.Interpretation;
import roth.ori.fling.symbols.Symbol;
import roth.ori.fling.symbols.GrammarElement;

public class VarArgs implements ParameterType {
  public final Class<?> aclazz;
  public final Class<?> clazz;
  public final Symbol nt;

  public <T> VarArgs(Class<T> clazz) {
    this.aclazz = Array.newInstance(clazz, 0).getClass();
    this.clazz = clazz;
    this.nt = null;
  }
  public VarArgs(Symbol nt) {
    this.aclazz = null;
    this.clazz = null;
    this.nt = nt;
  }
  public static <T> VarArgs varargs(Class<T> clazz) {
    return new VarArgs(clazz);
  }
  @Override public String toString() {
    return aclazz != null ? aclazz.getCanonicalName() : nt.name();
  }
  @Override public String toParameterString() {
    return nt != null ? nt.name() : clazz.getCanonicalName() + "...";
  }
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((aclazz == null) ? 0 : aclazz.hashCode());
    result = prime * result + ((nt == null) ? 0 : nt.hashCode());
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
    return aclazz != null ? aclazz.equals(other.aclazz) : nt.equals(other.nt);
  }
  @Override public boolean accepts(Object arg) {
    return clazz != null ? clazz.isInstance(arg) : nt.equals(arg) || ASTNode.class.isInstance(arg);
  }
  @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public List conclude(Object arg, BiFunction<GrammarElement, List, List> solution,
      String astPath) {
    List l = (List) ((List) arg).stream().map(x -> {
      // TODO Roth: check whether it make sense
      if (!(x instanceof Interpretation))
        return x;
      List $ = solution.apply(((Interpretation) x).symbol, ((Interpretation) x).value);
      assert $.size() == 1;
      return $.get(0);
    }).collect(Collectors.toList());
    Object[] $;
    try {
      // TODO Roth: getting nonterminal class name through HACKs
      $ = clazz != null ? (Object[]) Array.newInstance(clazz, l.size())
          : (Object[]) Array.newInstance(Class.forName(astPath + "$" + nt.name()), l.size());
    } catch (NegativeArraySizeException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    for (int i = 0; i < $.length; ++i)
      $[i] = l.get(i);
    return Collections.singletonList($);
  }
  // NOTE the returned classes should be duplicated according to input size
  @SuppressWarnings("rawtypes") @Override public List<Class> toClasses(Function<GrammarElement, Class> classSolution) {
    return nt != null ? nt.toClasses(classSolution) : Collections.singletonList(clazz);
  }
}
