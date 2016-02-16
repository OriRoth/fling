package automaton;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Stack;

import automaton.Example2.Z;

//public class Prolog {
//  static <X1,Y1, Z1, X extends P<X1, P<Y1, Z1>> > X f(X x) {return x;}
//  static <X2,Y2, Z2, X extends P<P<Y2, Z2>,X2> > X g(X x) { return x;}
//  public static void main(String[] args) {
//    P<P<String, Integer>,? extends P> x =null;
// P<P<String, Integer>, ? extends Object> g = g(x);
//    f(x);  
//  }
//}
//
//class P<X,Y> {}
abstract class Super{
  abstract X foo();
}
abstract class Sub extends Super{
  @Override abstract Z foo();
}
/**************************************************************************
 ****************************** EXAMPLE 1 *********************************
 *************************************************************************/
interface IorJ{
  <T> T foo(T a,T b);
}
interface I extends IorJ{
  String bar(I i);
  
} 
interface J extends IorJ{
  Integer bar(J j);
} 

interface I1 {
  <T > T i1(T a,T b);
} 
interface J1 {
  <T > T j1(T a,T b);
} 

interface L extends I,J{
  @Override <T> XY foo(T a, T b);
}
interface K extends I,J{
  @Override <T> XY foo(T a, T b);
}
interface L1 extends I1,J1{}
interface K1 extends I1,J1{}
class Example1{
  
  abstract static class Test{
    static L1 l1 = null;
    static K1 k1 = null;
    
    abstract <A> A choose(A a1,A a2);
    
    void test(L l , K k){
      I i = choose(l,k);
      J j = choose(l,k);
      IorJ ioj = choose(l,k);
      
      Integer s =choose(l,k).bar(choose(l,k));
      
      Object i2 = choose(l,k).foo(l1, k1);
      I1 _1 = choose(l, k).foo(l1,k1);
      J1 _2 = choose(l, k).foo(l1,k1);
      
//      choose(l, k).j();
      
//      check(choose(l,k));
    }
    
    abstract void check(I i);
    abstract void check(J j);
  }
}
/*///**************************************************************************
// ****************************** EXAMPLE 2 *********************************
// *************************************************************************/
class Example2{
  interface X1{}
  interface X2{}
  interface X3{}
  interface X4{}
  interface Y extends X1,X2{}
  interface Z extends X3,X4{}
  interface X extends Y,Z {}
  
  abstract class Test2{
   void test(X x){
     check(x);
   }
   abstract void check(X1 x);
   abstract void check(X2 x);
  }
}
///**************************************************************************
// ****************************** EXAMPLE 3 *********************************
// *************************************************************************/
abstract class Example3{
  interface P<L,R>{}
  interface Left<L,R,RR> extends P<P<L,R>,RR> {}
  interface Right<L,R,LL> extends P<LL,P<L,R>> {}
  
  abstract <A> A unify(A a1,A a2);
  
  abstract <L,R,X extends P<P<L,L>,R>,Y extends P<L,P<R,R>>> X foo(X a,Y y);
  <L,R> void bar(){
    
    P<P<Integer,Integer>,String> _1 = foo(null,null);
    
    P<P<P<Object, Object>, P<Object, Object>>, Object> _2 = foo(null,foo(null,null));
    
    P<P<P<Integer,Integer>, P<Integer,Integer>>, String> _3 = foo(null,foo(null,null));
    
    P<P<P<L,L>, P<L,L>>, R> _4 = foo(null,foo(null,null));
    
  }
  
  abstract <A> A makeBoundedTypeVar(A a);
  abstract <A> A makeUnboundedTypeVar();
  
  abstract <A> A f(A a, A a2);
  abstract <A,B> P<A,B> g(A a,B b);
  
  class Sub1 extends Super{} class Sub2 extends Super{} class Super{}
  <L,R> void wikipedia(){
    
     String _1 = unify(makeUnboundedTypeVar(),""); // inferred
     
     P<? extends Object, ? extends Object> _2 = unify(g("",5),g(null,null)); // inferred
     
     AbstractList<Object> _3 = unify(makeBoundedTypeVar(new Stack<>()),makeBoundedTypeVar(new ArrayList<>())); // inferred
     
     String _4 = unify(makeBoundedTypeVar(makeUnboundedTypeVar()),makeUnboundedTypeVar()); //manual
     
     P<String, Integer> _5 = unify(makeBoundedTypeVar(g("",5)),makeUnboundedTypeVar()); // inferred
     
     String choose = unify(f(makeUnboundedTypeVar(),null),f(null,""));
  }
}









