package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Collections;
import java.util.List;

public class Interfac<T, D, N> {
  @SuppressWarnings("rawtypes") private static final Interfac TOP = new Interfac();
  @SuppressWarnings("rawtypes") private static final Interfac BOT = new Interfac();
  public final N declaration;
  public final List<Method> methods;

  public Interfac(final N name, final List<Method> methods) {
    declaration = name;
    this.methods = Collections.unmodifiableList(methods);
  }

  private Interfac() {
    declaration = null;
    methods = null;
  }

  public N declaration() {
    return declaration;
  }

  public List<Method> methods() {
    return methods;
  }

  @SuppressWarnings("unchecked") public static <T, D, N> Interfac<T, D, N> top() {
    return TOP;
  }

  @SuppressWarnings("unchecked") public static <T, D, N> Interfac<T, D, N> bot() {
    return BOT;
  }

  public boolean isTop() {
    return this == TOP;
  }

  public boolean isBot() {
    return this == BOT;
  }
}
