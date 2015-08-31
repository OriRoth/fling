package org.spartan.fajita.api.parser.stack;

@SuppressWarnings("rawtypes") public class Stack<Head, Tail extends IStack> implements IStack<Head, Tail> {
  public final Head h;
  public final Tail t;

  public Stack(final Head h, final Tail t) {
    this.h = h;
    this.t = t;
  }
  public Stack<Integer, Stack<Head, Tail>> push(final Integer i) {
    return new Stack<>(i, this);
  }
  public Tail pop() {
    return t;
  }
  @SuppressWarnings("boxing") public static void main(final String[] args) {
    EmptyStack emptyStack = new EmptyStack();
    // push
    Stack<Integer, EmptyStack> lp1 = emptyStack.push(1);
    Stack<Integer, Stack<Integer, EmptyStack>> lp2 = lp1.push(2);
    Stack<Integer, Stack<Integer, Stack<Integer, EmptyStack>>> lp3 = lp2.push(3);
    System.out.println(emptyStack.peek() == null);
    System.out.println(lp1.peek() == 1);
    System.out.println(lp2.peek() == 2);
    System.out.println(lp3.peek() == 3);
    // pop
    // lp3.pop().pop().pop().pop();
  }
  @Override public Head peek() {
    return h;
  }
}
