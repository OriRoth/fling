package org.spartan.fajita.revision.util;

@FunctionalInterface public interface TriFunction<T, U, V, R> {
  R apply(T t, U u, V v);
}
