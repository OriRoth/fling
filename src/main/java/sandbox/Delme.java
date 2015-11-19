package sandbox;

import sandbox.Stack.Bottom;
import sandbox.Stack.Push;
import sandbox.Γ.γ1;
import sandbox.Γ.γ2;

class Γ {
  static class γ1 extends Γ {
  }

  static class γ2 extends Γ {
  }
}

abstract class Stack<Self extends Stack>{
  Γ top() {
    return null;
  }
  Stack pop() {
    return null;
  }
  <γ extends Γ> Push<γ, Self> push(γ g) {
    return null;
  }

  static class Bottom extends Stack<Bottom> {
  }

  static class Push<Head extends Γ, Tail extends Stack> extends Stack<Push<Head,Tail>> {
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
}

class Delme {
  public static void main(String[] args) {
    Push<γ1, Push<γ2, Push<γ1, Push<γ2, Stack.Bottom>>>> t;
    Push<γ1, Bottom> push = new Stack.Bottom().push((γ1)null);
    Push<γ2, Push<γ1, Bottom>> push2 = push.push((γ2)null);
    
  }
}

abstract class Qʹ {
  static final class ¤ extends Qʹ {
  }

  static abstract class Q<CurrentStack extends Stack, Top1 extends Γ, Top2 extends Γ> extends Qʹ {
    abstract Qʹ σ1();
    abstract Qʹ σ2();

    static abstract class q1<CurrentStack extends Stack, Top1 extends Γ, Top2 extends Γ> extends Q<CurrentStack, Top1, Top2> {
    }

    static final class q1_1_1<CurrentStack extends Stack> extends q1<CurrentStack, γ1, γ1> {
      Q σ1() {
        return null;
      }
      ¤ σ2() {
        return null;
      }
    }

    static class q1_1_2<CurrentStack extends Stack> extends q1<CurrentStack, γ1, γ2> {
    }

    static class q1_2_1<CurrentStack extends Stack> extends q1<CurrentStack, γ2, γ1> {
    }

    static class q1_2_2<CurrentStack extends Stack> extends q1<CurrentStack, γ2, γ2> {
    }

    static abstract class q2<CurrentStack extends Stack, Top1 extends Γ, Top2 extends Γ> extends Q<CurrentStack, Top1, Top2> {
    }

    static class q2_1_1<CurrentStack extends Stack> extends q2<CurrentStack, γ1, γ1> {
    }

    static class q2_1_2<CurrentStack extends Stack> extends q2<CurrentStack, γ1, γ2> {
    }

    static class q2_2_1<CurrentStack extends Stack> extends q2<CurrentStack, γ2, γ1> {
    }

    static class q2_2_2<CurrentStack extends Stack> extends q2<CurrentStack, γ2, γ2> {
    }
  }
