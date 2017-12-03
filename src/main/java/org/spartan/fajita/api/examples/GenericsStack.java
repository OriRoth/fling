package org.spartan.fajita.api.examples;

public class GenericsStack {
  interface B {
    S<B> push();
  }

  interface S<T> {
    S<S<T>> push();
    T pop();
  }
  static <T> T $() {
    return null;
  }

  public static void main(String[] args) {
    GenericsStack.<S<S<B>>> $().pop().pop().push();
  }
  // static <T> T $() {
  // return (T) new $$$();
  // }
  //
  // static class $$$ implements B, S {
  // @Override public $$$ pop() {
  // return this;
  // }
  // @Override public $$$ push() {
  // return this;
  // }
  // }
}
