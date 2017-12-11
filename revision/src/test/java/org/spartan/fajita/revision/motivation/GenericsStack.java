package org.spartan.fajita.revision.motivation;

public class GenericsStack {
  interface Stack {
    NonEmptyStack<?> push();
  }

  interface EmptyStack extends Stack {
    @Override NonEmptyStack<EmptyStack> push();
  }

  interface NonEmptyStack<Tail extends Stack> extends Stack {
    @Override NonEmptyStack<NonEmptyStack<Tail>> push();
    Tail pop();
  }

  static <T extends Stack> T initialize() {
    return null;
  }
  public static void main(String[] args) {
    NonEmptyStack<NonEmptyStack<EmptyStack>> $ = //
        GenericsStack.<NonEmptyStack<NonEmptyStack<EmptyStack>>> initialize();
    $ //
        .pop() //
        .pop() //
    // .pop() //
    ;
  }
}
