package roth.ori.fling.motivation;

@SuppressWarnings("all") public class GenericsStack {
  interface EmptyStack {
    NonEmptyStack<EmptyStack> push();
  }

  interface NonEmptyStack<Tail> {
    NonEmptyStack<NonEmptyStack<Tail>> push();
    Tail pop();
  }

  public static void main(String[] args) {
    NonEmptyStack<NonEmptyStack<EmptyStack>> $ = //
        (NonEmptyStack<NonEmptyStack<EmptyStack>>) null;
  }
}
