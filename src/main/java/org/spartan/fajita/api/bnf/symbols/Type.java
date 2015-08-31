package org.spartan.fajita.api.bnf.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Type {
  public final List<Class<?>> classes;

  public Type(final Class<?> clss1, final Class<?>... classes) {
    ArrayList<Class<?>> type = new ArrayList<>(Arrays.asList(classes));
    type.add(0, clss1);
    this.classes = type;
  }
  /**
   * parameterless method. (same as new Type(Void.class))
   */
  public Type() {
    classes = new ArrayList<>(Arrays.asList(Void.class));
  }
  @SuppressWarnings("unused") private Type(final boolean noType) {
    classes = new ArrayList<>();
  }
  @Override public String toString() {
    StringBuilder sb = new StringBuilder().append("(");
    classes.forEach(clss -> sb.append("'" + clss.getSimpleName() + "'").append(" X "));
    sb.delete(sb.length() - 3, sb.length());
    sb.append(")");
    return sb.toString();
  }
  @Override public boolean equals(final Object obj) {
    return obj.getClass() == Type.class && classes.equals(((Type) obj).classes);
  }
  @SuppressWarnings("boxing") @Override public int hashCode() {
    return 7 + classes.stream()
        .reduce(0, (n, clss) -> n + clss.hashCode(), (clss1, clss2) -> new Integer(clss1.hashCode()) + clss2.hashCode()).intValue();
  }

  // only for $ and epsilon , no class
  static final Type notype = new Type(true);
  public static final Type VOID = new Type(Void.class);
}
