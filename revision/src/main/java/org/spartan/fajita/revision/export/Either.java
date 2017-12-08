package org.spartan.fajita.revision.export;

public class Either {
  private final Object $;

  public Either(Object $) {
    this.$ = $;
  }
  public boolean is(Class<?> c) {
    return c.isInstance($);
  }
  @SuppressWarnings({ "unchecked", "unused" }) public <C> C get(Class<C> c) {
    return (C) $;
  }
  @Override public String toString() {
    return $.toString();
  }
}
