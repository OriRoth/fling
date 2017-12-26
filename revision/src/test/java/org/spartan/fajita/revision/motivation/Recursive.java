package org.spartan.fajita.revision.motivation;

// S ::= x (y x?)+
@SuppressWarnings({ "unchecked", "rawtypes" }) public class Recursive {
  interface S_<E> {
    S1_<E> x();
  }

  interface S1_<E> {
    S3_y<S2_<E>, R_S3_y<E>> y();
  }

  interface S2_<E> {
    S3_y<S2_<E>, R_S3_y<E>> y();
  }

  interface S3_y<E, y> {
    E x();
    y y();
    Object $();
  }

  interface R_S3_y<E> {
    S2_<E> x();
    R_S3_y<S2_<E>> y();
    Object $();
  }

  interface $ {
    Object $();
  }

  static class $$$ implements S_, S1_, S2_, S3_y, R_S3_y {
    @Override public $$$ y() {
      return this;
    }
    @Override public $$$ x() {
      return this;
    }
    @Override public Object $() {
      return new Object();
    }
  }

  static S_<$> go() {
    return new $$$();
  }
  public static void main(String[] args) {
    go().x().y().x().y().y().y().y().x().y().y().$();
  }
}
