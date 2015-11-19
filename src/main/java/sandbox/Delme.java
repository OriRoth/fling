package sandbox;

import sandbox.Γ.γ1;
import sandbox.Γ.γ2;

class Γ {
  static class γ1 extends Γ {
  }

  static class γ2 extends Γ {
  }
}

abstract class Stack<Self extends Stack> {
  Push<γ1, Self> γ1() {
    return push((γ1) null);
  }
  Push<γ2, Self> γ2() {
    return push((γ2) null);
  }
  Γ top() {
    return null;
  }
  Stack pop() {
    return null;
  }
  private <γ extends Γ> Push<γ, Self> push(γ g) {
    return null;
  }

  static class Bottom extends Stack<Bottom> {
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
  }
  public static void main(String[] args) {
    Push<γ1, Push<γ2, Push<γ1, Push<γ2, Stack.Bottom>>>> t;
    Push<γ1, Bottom> push = new Stack.Bottom().push((γ1) null);
    Push<γ2, Push<γ1, Bottom>> push2 = push.push((γ2) null);
    Push<γ1, Push<γ1, Push<γ2, Push<γ1, Push<γ1, Bottom>>>>> a = ((Stack.Bottom) null).γ1().γ1().γ2().γ1().γ1();
  }
}

