package automaton;

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

/**************************************************************************
 ****************************** EXAMPLE 1 *********************************
 *************************************************************************/
class Example1{
  interface I {} interface J{}
  interface L extends I,J{}
  interface K extends I,J{}
  
  
  abstract class Test{
    <A> A choose(A a1,A a2){
      return a1.hashCode() < a2.hashCode() ? a1 : a2;
    }
    void test(L l , K k){
      I i = choose(l,k);
      J j = choose(l,k);

      check(choose(l,k));
    }
    abstract void check(I i);
    abstract void check(J j);
  }
}
/**************************************************************************
 ****************************** EXAMPLE 2 *********************************
 *************************************************************************/
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
/**************************************************************************
 ****************************** EXAMPLE 3 *********************************
 *************************************************************************/
abstract class Example3{
  interface P<L,R>{}
  interface Left<L,R,RR> extends P<P<L,R>,RR> {}
  interface Right<L,R,LL> extends P<LL,P<L,R>> {}
  interface X<A,B,C> extends Left<A,B,C>,Right<A,B,C>{}
  
  
  abstract <L,R,X extends P<P<L,L>,R>,Y extends P<L,P<R,R>>> X foo(X a,Y y);
  void bar(){
    P<P<Integer,Integer>,String> _1 = foo(null,null);
    P<P<P<Object, Object>, P<Object, Object>>, Object> _2 = foo(null,foo(null,null));
    P<P<P<Object, Object>, P<Object, Object>>, Object> _3 = foo(null,foo(null,null));
  }
}