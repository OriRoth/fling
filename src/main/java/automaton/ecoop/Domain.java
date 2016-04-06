package automaton.ecoop;

import automaton.ecoop.Domain.MultiPush.DoubleP.DoubleP_γ1_γ1;
import automaton.ecoop.Domain.MultiPush.DoubleP.DoubleP_γ1_γ2;
import automaton.ecoop.Domain.MultiPush.NoOp.NoOp2;
import automaton.ecoop.Domain.MultiPush.SingleP.SingleP_γ2;
import automaton.ecoop.Domain.Q.q0;
import automaton.ecoop.Domain.Q.q1.q1_γ1;
import automaton.ecoop.Domain.Q.q1.q1_γ2;
import automaton.ecoop.Domain.Q.q2.q2_E;
import automaton.ecoop.Domain.Q.q3.q3_γ2;
import automaton.ecoop.Domain.R.r1;
import automaton.ecoop.Domain.R.r2;
import automaton.ecoop.Domain.Stack.E;
import automaton.ecoop.Domain.Stack.P;
import automaton.ecoop.Domain.Γʹ.Γ;
import automaton.ecoop.Domain.Γʹ.Γ.γ1;
import automaton.ecoop.Domain.Γʹ.Γ.γ2;
/**
 * This is were we place code that must compile correctly. To be extracted and
 * placed as part of LaTeX documents.
 * 
 * @author Yossi Gil
 * 
 */
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
  // id.listing
  interface ID<T extends ID<?>> {
    default T id() { return null; }
  }
  class A implements ID<A> { /**/ }
  abstract class B<Z extends B<?>> implements ID<Z> { /**/ }
  class C extends B<C> { /**/ }

  // stack.listing
  public static abstract class Stack<Rest extends Stack<?>> { 
    public abstract Γʹ top();      
    public abstract Rest pop();    
    public abstract Stack<?> γ1();  // Push type ¢\cc{\scriptsize $\Gamma'.\Gamma.\gamma1$}¢
    public abstract Stack<?> γ2();  // Push type ¢\cc{\scriptsize $\Gamma'.\Gamma.\gamma2$}¢ 
    public static class P<Top extends Γ, Rest extends Stack<?>> 
      extends Stack<Rest> { // Type of a non-empty stack: 
         @Override public Top top() { return null; } 
         @Override public Rest pop() { return null; }
         @Override public P<γ1, P<Top,Rest>> γ1() { return null; } 
         @Override public P<γ2, P<Top,Rest>> γ2() { return null; }
    }
    public static final class E 
      extends Stack<¤> { // Type of an empty stack
        @Override public Γʹ.¤ top() { return null; }
        @Override public ¤ pop() { return null; } 
        @Override public P<γ1, E> γ1() { return null; } 
        @Override public P<γ2, E> γ2() { return null; }
    }
    public static final E empty = null; 
    private static final class ¤ 
      extends Stack<¤> { // Type of pop from empty stack 
        @Override public Γʹ.¤ top() { return null; }
        @Override public ¤ pop() { return null; } 
        @Override public ¤ γ1() { return null; } 
        @Override public ¤ γ2() { return null; }
    }
  }
  //@Formatter:on
  private interface Pʹ<
  // ¢$2+k+1$¢ generic arguments: 
    Top extends Γ,   
    Rest extends JS, 
    J_γ1 extends JS, 
    J_γ2 extends JS,
    Me extends JS
  > extends JS<Rest, J_γ1, J_γ2> {
     @Override public Top top();
     @Override P<γ1, Me, Me, J_γ2> γ1();
     @Override P<γ2, Me, J_γ2, Me> γ2();
  }

  public interface JS<  // ¢$1+k$¢ generic parameters
    //As a convention, we use JS with its raw type when no parameters are introduced 
      Rest extends JS, 
      J_γ1 extends JS, 
      J_γ2 extends JS 
  > {
    Γʹ top();
    Rest pop();
    JS γ1();
    JS γ2();
    J_γ1 jump_γ1();
    J_γ2 jump_γ2();
    interface ¤ extends JS<¤, ¤, ¤> {
      @Override public Γʹ.¤ top();
      @Override public ¤ pop();
      @Override public ¤ γ1();
      @Override public ¤ γ2();
    }
    public interface E extends JS<¤, ¤, ¤> {
      @Override public Γʹ.¤ top();
      @Override public ¤ pop();
      @Override public P<γ1, E, E, ¤> γ1();
      @Override public P<γ2, E, ¤, E> γ2();
    }
    public static final E empty = null;
    public interface P<// ¢$2+k$¢ generic arguments:
        Top extends Γ, 
        Rest extends JS,    
        J_γ1 extends JS, 
        J_γ2 extends JS
    > extends Pʹ<Top, Rest, J_γ1, J_γ2, 
          P<Top, Rest, J_γ1, J_γ2>
    > { /**/ }
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
    private static final class ¤ extends Q<Stack<?>, Γʹ.¤> {/**/}
    public static final class q0 extends Q<E,Γʹ.¤>{
      public SingleP_γ2<E> σ1() { return null; }
    }
    public static abstract class q1<S extends Stack<?> , T extends Γʹ> extends Q<S,T> {
      private static final class ¤ extends q1<E, Γʹ.¤> {/**/}
      public static final class q1_γ1<Rest extends Stack<?>> extends q1<P<γ1,Rest>,γ1> {/**/}
      public static final class q1_γ2<Rest extends Stack<?>> extends q1<P<γ2,Rest>,γ2> {
        public NoOp2<Rest> σ1() { return null; }
      }
    }
    public static abstract class q2<S extends Stack<?> , T extends Γʹ> extends Q<S,T> {
      public static final class q2_E extends q2<E, Γʹ.¤> {
        public DoubleP_γ1_γ2 σ1() { return null; }
      }
      public static final class q2_γ1<S extends Stack<?>> extends q2<S,γ1> {/**/}
      public static final class q2_γ2<S extends Stack<?>> extends q2<S,γ2> {
        public DoubleP_γ1_γ1<S> σ1() { return null; }
      }
    }
    public static abstract class q3<S extends Stack<?>, T extends Γʹ> extends Q<S,T> {
      public static final class q3_γ1<Rest extends Stack<?>> extends q3<P<γ1,Rest>,γ1> {/**/}
      public static final class q3_γ2<Rest extends Stack<?>> extends q3<P<γ2,Rest>,γ2> {/**/}
      private static final class ¤ extends q3<E, Γʹ.¤> {/**/}
    }
  }
  // Γʹ.¤
  static abstract class MultiPush<S extends Stack<?>>{
    abstract Q<?,?> go();
    static abstract class NoOp<S extends Stack<?>> extends MultiPush<S>{
      static final class NoOp1<S extends P<γ2,?>> extends NoOp<S>{
        @Override q3_γ2<S> go() { return null; }
      }
      static final class NoOp2<S extends Stack<?>> extends NoOp<S>{
        @Override q2_E go() { return null; }
      }      
    }
    static abstract class SingleP<S extends Stack<?>> extends MultiPush<S>{
      static final class SingleP_γ2<S extends Stack<?>> extends SingleP<S>{
        @Override q1_γ2<S> go() { return null; }
      }     
    }
    static abstract class DoubleP<S extends Stack<?>> extends MultiPush<S>{
      static final class DoubleP_γ1_γ1<S extends Stack<?>> extends DoubleP<S>{
        @Override q1_γ1<P<γ1,P<γ1,S>>> go() { return null; }
      }
      static final class DoubleP_γ1_γ2 extends DoubleP<E>{
        @Override q3_γ2<P<γ2,P<γ1,E>>> go() { return null; }
      }
    }
  }
  @SuppressWarnings("null")
  public static void main(String[] args) {
    q0 x1 = null;
    SingleP_γ2<E> x2 = x1.σ1();
    q1_γ2<E> x3 = x2.go();
    NoOp2<E> x4 = x3.σ1();
    q2_E x5 = x4.go();
    DoubleP_γ1_γ2 x6 = x5.σ1();
    q3_γ2<P<γ2, P<γ1, E>>> go = x6.go();
  }
}
//@formatter:on
