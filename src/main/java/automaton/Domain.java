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
  
  public static abstract class Γʹ { 
    private static abstract class ¤ extends Γʹ {
      // Empty private class, cannot be used by clients.
      private ¤() { /* Private constructor hinders extension by clients */ }
    }
    public  static abstract class Γ extends Γʹ { 
      public abstract Γʹ g(); 
      public static final class γ1 extends Γ {
        // Covariant return type in overriding:
        @Override public γ2 g() { return null; } 
      }
      public static final class γ2 extends Γ {
        // Covariant return type in overriding:
        @Override  public Γʹ.¤ g() { return null; }
      }
    }
  } 
  
  
  
  //stack.listinpublic public g
  
  public static abstract class Stack<Tail extends Stack<?>> { 
    public abstract Tail pop(); 
    public abstract Γʹ top();
    public abstract Stack<?> γ1(); 
    public abstract Stack<?> γ2();
    public static final class B extends Stack<¤> {
      @Override public ¤ pop() { return null; } 
      @Override public Γʹ.¤ top() { return null; }
      @Override public P<γ1, B> γ1() { return null; } 
      @Override public P<γ2, B> γ2() { return null; }
    }
    private static final class ¤ extends Stack<¤> {
      @Override public ¤ pop() { return null; } 
      @Override public Γʹ.¤ top() { return null; }
      @Override public ¤ γ1() { return null; } 
      @Override public ¤ γ2() { return null; }
    }
    // Type constructor making it possible to push an unlimited number of items (types) into the stack: 
    public static class P<Head extends Γ, Tail extends Stack<?>> extends Stack<Tail> {
      @Override public Tail pop() { return null; }
      @Override public Head top() { return null; } 
      @Override public P<γ1, P<Head,Tail>> γ1() { return null; } 
      @Override public P<γ2, P<Head,Tail>> γ2() { return null; }
    }
  }

  
  //binary-function.listing
  public static abstract class f { // Starting point of fluent API
    public static r1 r1() { return null; }
    public static r2 r2() { return null; }
  }  
  public static abstract class R { 
    public abstract Γʹ s1();
    public abstract Γʹ s2();
    public static final class r1 extends R {
      @Override public γ1 s1() { return null; } 
      @Override public γ2 s2() { return null; }
    }
    public static final class r2 extends R {
      @Override public γ2 s1() { return null; }
      @Override public Γʹ.¤ s2() { return null; }
    }
  }



  
  

 

  public static abstract class Q<S extends Stack<?>, Top extends Γʹ> {
    public static private final class ¤ extends Q<Stack<?>, Γʹ.¤> {/**/}
    public static final class q0 extends Q<B,Γʹ.¤>{
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
