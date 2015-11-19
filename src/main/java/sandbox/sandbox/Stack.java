package sandbox.sandbox;

import sandbox.sandbox.Q.q1;
import sandbox.sandbox.Q.q2;
import sandbox.sandbox.Stack.Push;
import sandbox.sandbox.Γʹ.Γ;
import sandbox.sandbox.Γʹ.Γ.γ1;
import sandbox.sandbox.Γʹ.Γ.γ2;

class Γʹ { //@formatter:off
  static final class ¤ extends Γʹ {}
  static abstract class Γ extends Γʹ { 
    static final class γ1 extends Γ {}
    static final class γ2 extends Γ {}
  }
} //@formatter:on

class Q<Stack, Top> { //@formatter:off
  private Stack stack;
  private Top top;
  Q(Stack stack, Top top) { this.stack = stack; this.top = top;}
  static class q1<Stack, Top> extends Q<Stack, Top> {}
  static class q2<S, Top> extends Q<S, Top> {}
  static class q1_1<X extends Γ, Rest extends Stack> extends q1<Push<X, Rest>, γ1> {
    q2 σ1() {
      return stack.q2(); 
    }
  }
  static class q1_2<Stack> extends q1<Stack, γ2> {}
  static class q2_1<Stack> extends q2<Stack, γ1> {}
  static class q2_2<Stack> extends q2<Stack, γ2> {}
} //@formatter:on

abstract class Stack<Self extends Stack> {
  abstract Stack pop();
  abstract Γʹ top();
  Push<γ1, Self> γ1() {
    return push((γ1) null);
  }
  Push<γ2, Self> γ2() {
    return push((γ2) null);
  }

  static final class ¤ extends Stack<¤> {
    ¤ pop() {
      return null;
    }
    Γʹ.¤ top() {
      return null;
    }
  }

  private <γ extends Γ> Push<γ, Self> push(γ g) {
    return null;
  }

  static class Push<Head extends Γ, Tail extends Stack> extends Stack<Push<Head, Tail>> {
    Head head;
    Tail tail;

    Push(Head head, Tail tail) {
      this.head = head;
      this.tail = tail;
    }
    Head top() {
      return null;
    }
    Tail pop() {
      return tail;
    }
    q1<Push<Head, Tail>, Head> q1() {
      return null;
    }
    q2<Push<Head, Tail>, Head> q2() {
      return null;
    }
  }

  public static void main(String[] args) {
    Push<γ1, Push<γ2, Push<γ1, Push<γ2, Stack.¤>>>> t;
    Push<γ1, ¤> push = new Stack.¤().γ1();
    Push<γ2, Push<γ1, ¤>> push2 = push.γ2();
    Push<γ1, Push<γ1, Push<γ2, Push<γ1, Push<γ1, ¤>>>>> a = ((Stack.¤) null).γ1().γ1().γ2().γ1().γ1();
    Push<γ1, Push<γ2, Push<γ1, Push<γ1, ¤>>>> b = a.pop();
    Push<γ2, Push<γ1, Push<γ1, ¤>>> c = b.pop();
    Push<γ1, Push<γ1, ¤>> d = c.pop();
    Push<γ1, ¤> e = d.pop();
    ¤ f = e.pop();
    ¤ g = f.pop();
  }
}
