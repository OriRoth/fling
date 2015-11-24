package automaton;
import automaton.Domain.Stack;
import automaton.Domain.Stack.Bottom;
import automaton.Domain.Stack.Push;
import automaton.Domain.Γʹ;
import automaton.Domain.Γʹ.Γ;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;
import automaton.Pusher.DoublePush.DoublePush1;
import automaton.Pusher.DoublePush.DoublePush_γ1_γ2;
import automaton.Pusher.NoOp.NoOp2;
import automaton.Pusher.SinglePush.SinglePush1;
import automaton.Q.q0;
import automaton.Q.q1.q1_γ2;
import automaton.Q.q17.q17_;
import automaton.Q.q25.q25_γ1;
import automaton.Q.q25.q25_γ2;
//@formatter:off
@SuppressWarnings({"static-method","unused"}) 
public class Domain { 
  static abstract class Γʹ { 
    static private final class ¤ extends Γʹ {/**/}
    static abstract class Γ extends Γʹ { 
      static final class γ1 extends Γ {/**/}
      static final class γ2 extends Γ {/**/}
    }
  } 
  static abstract class Stack<Tail extends Stack<?>> { 
    abstract Tail pop(); abstract Γʹ top();
    abstract Stack<?> γ1(); abstract Stack<?> γ2();
    static final class Bottom extends Stack<¤> {
      ¤ pop() { return null; } Γʹ.¤ top() { return null; }
      Push<γ1, Bottom> γ1() { return null; } Push<γ2, Bottom> γ2() { return null; }
    }
    static private final class ¤ extends Stack<¤> {
      ¤ pop() { return null; } Γʹ.¤ top() { return null; }
      ¤ γ1() { return null; } ¤ γ2() { return null; }
    }
    static class Push<Head extends Γ, Tail extends Stack<?>> extends Stack<Tail> {
      Head top() { return null; } Tail pop() { return null; }
      Push<γ1, Push<Head,Tail>> γ1() { return null; } Push<γ2, Push<Head,Tail>> γ2() { return null; }
    }
  }

    
    public static void main(String[] args) {
      Push<γ1, Push<γ2, Push<γ1, Push<γ2, Bottom>>>> t;
      
      Bottom x0 = new Bottom();
      Push<γ1,Bottom> x1 = x0.γ1();
      Push<γ2,Push<γ1,Bottom>> x2 = x1.γ2();
      Push<γ1,Push<γ1,Push<γ2,Push<γ1,Push<γ1,Bottom>>>>> x3 = ((Bottom) null).γ1().γ1().γ2().γ1().γ1();
      Push<γ1,Push<γ2,Push<γ1,Push<γ1,Bottom>>>> x4 = x3.pop();
      Push<γ2,Push<γ1,Push<γ1,Bottom>>> x5 = x4.pop();
      Push<γ1,Push<γ1,Bottom>> x6 = x5.pop();
      Push<γ1,Bottom> x7 = x6.pop();
      Bottom x8 = x7.pop();
      ¤ x9 = x8.pop();
  } 
  }

  static abstract class Q<S extends Stack<?>, Top extends Γʹ> {
    static private final class ¤ extends Q<Stack<?>, Γʹ.¤> {/**/}
    static final class q0 extends Q<Bottom,Γʹ.¤>{
      SinglePush1<Bottom> σ1() {
        return null;
       }
     }
    
    static abstract class q1<S extends Stack<?>, T extends Γʹ> extends Q<S, T> {
      static final class q1_γ1<Rest extends Stack<?>> extends q1<Push<γ1,Rest>, γ1> {/**/}
      static final class q1_γ2<Rest extends Stack<?>> extends q1<Push<γ2,Rest>, γ2> {/**/}
      static private final class ¤ extends q1<Bottom, Γʹ.¤> {/**/}
    }
    static abstract class q2<S extends Stack<?>, T extends Γʹ> extends Q<S, T> {
      static final class q2_γ1<S extends Stack<?>> extends q2<S, γ1> {/**/}
      static final class q2_γ2<S extends Stack<?>> extends q2<S, γ2> {/**/}
      static private final class ¤ extends q2<Bottom, Γʹ.¤> {/**/}
    }
    static abstract class q17<S extends Stack<?> , T extends Γʹ> extends Q<S,T> {
      static final class q17_ extends q17<Bottom, Γʹ.¤> {
        DoublePush_γ1_γ2 σ1() { return null; }
      }
      static final class q17_γ1<S extends Stack<?>> extends q17<S, γ1> {/**/}
      static final class q17_γ2<S extends Stack<?>> extends q17<S, γ2> {
        DoublePush1<S> σ1() { return null; }
      }
    }
    static abstract class q25<S extends Stack<?> , T extends Γʹ> extends Q<S,T> {
      static private final class ¤ extends q25<Bottom, Γʹ.¤> {/**/}
      
      static final class q25_γ1<Rest extends Stack<?>> extends q25<Push<γ1,Rest>, γ1> {/**/}
      
      static final class q25_γ2<Rest extends Stack<?>> extends q25<Push<γ2,Rest>, γ2> {
        NoOp2<Rest> σ1() { return null; }
      }
    }
  }
  // Γʹ.¤
  static abstract class Pusher<S extends Stack<?>>{
    abstract Q<?,?> go();
    
    static abstract class NoOp<S extends Stack<?>> extends Pusher<S>{
      static final class NoOp1<S extends Push<γ2,?>> extends NoOp<S>{
        @Override q1_γ2<S> go() { return null; }
      }
      static final class NoOp2<S extends Stack<?>> extends NoOp<S>{
        @Override q17_ go() { return null; }
      }      
    }
    
    static abstract class SinglePush<S extends Stack<?>> extends Pusher<S>{
      static final class SinglePush1<S extends Stack<?>> extends SinglePush<S>{
        @Override q25_γ2<S> go() { return null; }
      }     
    }
    
    static abstract class DoublePush<S extends Stack<?>> extends Pusher<S>{
      static final class DoublePush1<S extends Stack<?>> extends DoublePush<S>{
        @Override q25_γ1<Push<γ1,Push<γ1,S>>> go() { return null; }
      }
      static final class DoublePush_γ1_γ2 extends DoublePush<Bottom>{
        @Override q1_γ2<Push<γ2,Push<γ1,Bottom>>> go() { return null; }
        
      }
    }

  }

  @SuppressWarnings("null")
  public static void main(String[] args) {
    q0 x1 = null;
    SinglePush1<Bottom> x2 = x1.σ1();
    q25_γ2<Bottom> x3 = x2.go();
    NoOp2<Bottom> x4 = x3.σ1();
    q17_ x5 = x4.go();
    DoublePush_γ1_γ2 x6 = x5.σ1();
    q1_γ2<Push<γ2, Push<γ1, Bottom>>> go = x6.go();
  }
}
 
  
  
  
//@formatter:on
