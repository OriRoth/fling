package sandbox;
import sandbox.Γʹ.Γ;
import sandbox.Γʹ.Γ.γ1;
import sandbox.Γʹ.Γ.γ2;

class Γʹ {
  static final class ¤ extends Γʹ {}
  static abstract class Γ extends Γʹ {
    static final class γ1 extends Γ {
    }
    static final class γ2 extends Γ {
    }
  }
}

abstract class Stack<Self extends Stack> {
  abstract Stack pop();
  abstract  Γʹ top();
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