package org.spartan.fajita.api.examples;

@SuppressWarnings({ "rawtypes", "synthetic-access" }) public class GenericStack {
  public interface B {
    S<B> push();
  }

  public interface S<T> {
    T pop();
    S<S<T>> push();
  }

  private static class $$$ implements B, S {
    @Override public $$$ pop() {
      return this;
    }
    @Override public $$$ push() {
      return this;
    }
  }

  public static B bottom() {
    return new $$$();
  }
  public static void main(String[] args) {
    bottom().push().push().pop().pop().push();
    System.out.println("Compile AND run");
  }
}
