package org.spartan.fajita.revision.motivation;

@SuppressWarnings({ "unchecked", "rawtypes" }) public class AnBn {
  interface S_<E> {
    S_b<B<E>, E> a();
    Object $();
  }

  interface B<E> {
    E b();
  }

  interface S_b<E, b> {
    S_b<B<E>, E> a();
    b B();
  }

  interface $ {
    Object $();
  }

  static class $$$ implements S_, B, S_b, $ {
    @Override public $$$ B() {
      return this;
    }
    @Override public $$$ b() {
      return this;
    }
    @Override public $$$ a() {
      return this;
    }
    @Override public $$$ $() {
      return this;
    }
  }

  static S_<$> go() {
    return new $$$();
  }
  public static void main(String[] args) {
    go().a().a().a().B().b().b().$();
  }
}
