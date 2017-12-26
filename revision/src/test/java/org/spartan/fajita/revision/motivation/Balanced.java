package org.spartan.fajita.revision.motivation;

@SuppressWarnings({ "unchecked", "rawtypes" }) public class Balanced {
  interface S_$<E> {
    Object $();
    S_c<c_<S_$<E>>, S_$<E>> o();
  }

  interface S_c<E, c> {
    S_c<c_<S_c<E, c>>, S_c<E, c>> o();
    c c();
  }

  interface c_<E> {
    E c();
  }

  interface $ {
    Object $();
  }

  static class $$$ implements S_$, c_, S_c, $ {
    @Override public $$$ c() {
      return this;
    }
    @Override public $$$ o() {
      return this;
    }
    @Override public $$$ $() {
      return this;
    }
  }

  static S_$<$> go() {
    return new $$$();
  }
  public static void main(String[] args) {
    go() //
        .o() //
        .c() //
        .o() //
        /**/.o()/**//**/.o() //
        /**//**/.c() //
        /**/.c() //
        /**/.o() //
        /**/.c() //
        .c() //
        .$();
  }
}
