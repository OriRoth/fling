package automaton;
import automaton.Domain.Per.DoubleP.DoubleP1;
import automaton.Domain.Per.DoubleP.DoubleP_γ1_γ2;
import automaton.Domain.Per.NoOp.NoOp2;
import automaton.Domain.Per.SingleP.SingleP1;
import automaton.Domain.Q.q0;
import automaton.Domain.Q.q1.q1_γ2;
import automaton.Domain.Q.q17.q17_;
import automaton.Domain.Q.q25.q25_γ1;
import automaton.Domain.Q.q25.q25_γ2;
import automaton.Domain.R.r1;
import automaton.Domain.R.r2;
import automaton.Domain.Stack.B;
import automaton.Domain.Stack.P;
import automaton.Domain.Γʹ.Γ;
import automaton.Domain.Γʹ.Γ.γ1;
import automaton.Domain.Γʹ.Γ.γ2;
//@formatter:off
@SuppressWarnings({"static-method","unused"}) 
public class Domain { 
  
  
  // gamma.listing
  
  static abstract class Γʹ { 
    static private abstract class ¤ extends Γʹ {
      // Empty private class, cannot be used by clients.
      private ¤() {
       // Class cannot be extended 
      }
    }
    static abstract class Γ extends Γʹ { 
      abstract Γʹ g(); 
      static final class γ1 extends Γ {
        // Covariant return type in overriding:
        @Override  γ2 g() { return null; } 
      }
      static final class γ2 extends Γ {
        // Covariant return type in overriding:
        @Override  Γʹ.¤ g() { return null; }
      }
    }
  } 
  
  
  //gamma-example.listing

  public static void demo_of_unary_function_g() {
    γ2 _1 = new γ1().g();  // ✓
    γ1 _2 = new γ2().g();  // ✗ type mismatch
    Γʹ.¤  _3 = new γ2().g();  // ✗ class ¤ is private
    Γʹ _4 = new γ2().g();  // ✓
    _4.g();  // ✗ method g() is undefined in type Γʹ
  } 
  
  
  //stack.listing
  
  static abstract class Stack<Tail extends Stack<?>> { 
    abstract Tail pop(); 
    abstract Γʹ top();
    abstract Stack<?> γ1(); 
    abstract Stack<?> γ2();
    static final class B extends Stack<¤> {
      ¤ pop() { return null; } 
      Γʹ.¤ top() { return null; }
      P<γ1, B> γ1() { return null; } 
      P<γ2, B> γ2() { return null; }
    }
    static private final class ¤ extends Stack<¤> {
      ¤ pop() { return null; } 
      Γʹ.¤ top() { return null; }
      ¤ γ1() { return null; } 
      ¤ γ2() { return null; }
    }
    static class P<Head extends Γ, Tail extends Stack<?>> extends Stack<Tail> {
      Head top() { return null; } 
      Tail pop() { return null; }
      P<γ1, P<Head,Tail>> γ1() { return null; } 
      P<γ2, P<Head,Tail>> γ2() { return null; }
    }
  }

  
  //binary-function.listing
  static abstract class f {
    // A representation of the set ¢$S$¢
    static r1 r1() { return null; }
    static r2 r2() { return null; }
  }  
  static abstract class R {
    abstract Γʹ s1();
    abstract Γʹ s2();
    static final class r1 extends R {
      @Override γ1 s1() { return null; } 
      @Override γ2 s2() { return null; }
    }
    static final class r2 extends R {
      @Override γ2 s1() { return null; }
      @Override Γʹ.¤ s2() { return null; }
    }
  }


  
  //binary-function-example.listing
  
  public static void demonstration_of_binary_function_f(){
    γ1 _1 = f.r1().s1();     // ✓
    γ2 _2 = f.r1().s2();     // ✓
    γ2 _3 = f.r2().s1();     // ✓
    f.r2().s2().g(); // ✗ method s2() is undefined for type Γʹ
  }
  
  

  public static void main2(String[] args) {
    P<γ1, P<γ2, P<γ1, P<γ2, B>>>> t;
      
    B x0 = new B();
    P<γ1,B> x1 = x0.γ1();
    P<γ2,P<γ1,B>> x2 = x1.γ2();
    P<γ1,P<γ1,P<γ2,P<γ1,P<γ1,B>>>>> x3 = ((B) null).γ1().γ1().γ2().γ1().γ1();
    P<γ1,P<γ2,P<γ1,P<γ1,B>>>> x4 = x3.pop();
    P<γ2,P<γ1,P<γ1,B>>> x5 = x4.pop();
    P<γ1,P<γ1,B>> x6 = x5.pop();
    P<γ1,B> x7 = x6.pop();
    B x8 = x7.pop();
    ¤ x9 = x8.pop();
  } 

  static abstract class Q<S extends Stack<?>, Top extends Γʹ> {
    static private final class ¤ extends Q<Stack<?>, Γʹ.¤> {/**/}
    static final class q0 extends Q<B,Γʹ.¤>{
      SingleP1<B> σ1() {
        return null;
       }
     }
    
    static abstract class q1<S extends Stack<?>, T extends Γʹ> extends Q<S, T> {
      static final class q1_γ1<Rest extends Stack<?>> extends q1<P<γ1,Rest>, γ1> {/**/}
      static final class q1_γ2<Rest extends Stack<?>> extends q1<P<γ2,Rest>, γ2> {/**/}
      static private final class ¤ extends q1<B, Γʹ.¤> {/**/}
    }
    static abstract class q2<S extends Stack<?>, T extends Γʹ> extends Q<S, T> {
      static final class q2_γ1<S extends Stack<?>> extends q2<S, γ1> {/**/}
      static final class q2_γ2<S extends Stack<?>> extends q2<S, γ2> {/**/}
      static private final class ¤ extends q2<B, Γʹ.¤> {/**/}
    }
    static abstract class q17<S extends Stack<?> , T extends Γʹ> extends Q<S,T> {
      static final class q17_ extends q17<B, Γʹ.¤> {
        DoubleP_γ1_γ2 σ1() { return null; }
      }
      static final class q17_γ1<S extends Stack<?>> extends q17<S, γ1> {/**/}
      static final class q17_γ2<S extends Stack<?>> extends q17<S, γ2> {
        DoubleP1<S> σ1() { return null; }
      }
    }
    static abstract class q25<S extends Stack<?> , T extends Γʹ> extends Q<S,T> {
      static private final class ¤ extends q25<B, Γʹ.¤> {/**/}
      
      static final class q25_γ1<Rest extends Stack<?>> extends q25<P<γ1,Rest>, γ1> {/**/}
      
      static final class q25_γ2<Rest extends Stack<?>> extends q25<P<γ2,Rest>, γ2> {
        NoOp2<Rest> σ1() { return null; }
      }
    }
  }
  // Γʹ.¤
  static abstract class Per<S extends Stack<?>>{
    abstract Q<?,?> go();
    
    static abstract class NoOp<S extends Stack<?>> extends Per<S>{
      static final class NoOp1<S extends P<γ2,?>> extends NoOp<S>{
        @Override q1_γ2<S> go() { return null; }
      }
      static final class NoOp2<S extends Stack<?>> extends NoOp<S>{
        @Override q17_ go() { return null; }
      }      
    }
    
    static abstract class SingleP<S extends Stack<?>> extends Per<S>{
      static final class SingleP1<S extends Stack<?>> extends SingleP<S>{
        @Override q25_γ2<S> go() { return null; }
      }     
    }
    
    static abstract class DoubleP<S extends Stack<?>> extends Per<S>{
      static final class DoubleP1<S extends Stack<?>> extends DoubleP<S>{
        @Override q25_γ1<P<γ1,P<γ1,S>>> go() { return null; }
      }
      static final class DoubleP_γ1_γ2 extends DoubleP<B>{
        @Override q1_γ2<P<γ2,P<γ1,B>>> go() { return null; }
        
      }
    }

  }

  @SuppressWarnings("null")
  public static void main(String[] args) {
    q0 x1 = null;
    SingleP1<B> x2 = x1.σ1();
    q25_γ2<B> x3 = x2.go();
    NoOp2<B> x4 = x3.σ1();
    q17_ x5 = x4.go();
    DoubleP_γ1_γ2 x6 = x5.σ1();
    q1_γ2<P<γ2, P<γ1, B>>> go = x6.go();
  }
}
 
  
  
  
//@formatter:on
