package automaton;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Stack;

import org.spartan.fajita.api.Main;

// public class Prolog {
// static <X1,Y1, Z1, X extends P<X1, P<Y1, Z1>> > X f(X x) {return x;}
// static <X2,Y2, Z2, X extends P<P<Y2, Z2>,X2> > X g(X x) { return x;}
// public static void main(String[] args) {
// P<P<String, Integer>,? extends P> x =null;
// P<P<String, Integer>, ? extends Object> g = g(x);
// f(x);
// }
// }
//
// class P<X,Y> {}
// formatter:off
class U<X extends U<X>> {
  /****/
  U<X> self() {
    return this;
  };
}

interface client {
  static void f() {
    class A<X extends U<? extends X>> extends U<? extends X> { }
    class B extends A<B> {}
    final B b = null;
    final A<?> a5 = b;
    final A<B> a4 = b;
    final A<A<B>> a3 = b;
  }
}

interface X {
  /****/
}

interface Y {
  /****/
}

interface Z extends X {
  /****/
}

interface XY extends X, Y {
  /****/
}

interface Super {
  abstract X foo();
}

interface Sub extends Super {
  @Override abstract Z foo();
}

/**************************************************************************
 ****************************** EXAMPLE 1 *********************************
 *************************************************************************/
interface IorJ {
  <T> T foo(T a, T b);
}

interface I extends IorJ {
  String bar(I i);
}

interface J extends IorJ {
  Integer bar(J j);
}

interface L extends I, J {
  @Override <T> T foo(T a, T b);
}

interface K extends I, J {
  @Override <T> T foo(T a, T b);
}

class Example1 {
  abstract static class Test {
    static L l = null;
    static K k = null;

    abstract <A> A unify(A a1, A a2);
    void test(L l, K k) {
      IorJ ioj = unify(l, k); // Weak unification
      asI(unify(l, k)); // Unification with downcasting to I
      asJ(unify(l, k)); // Unification with downcasting to J
      // Note: there are types which extends both I and J, but the result is not
      // any of these types.
      // L l1= unify(l, k);
      // K k1= unify(l, k);
      String s = asI(unify(l, k)).bar((null));
      Integer i = asJ(unify(l, k)).bar((null));
      asInteger(asJ(unify(l, k)).bar((null)));
      asInteger((unify(l, k)).bar(null));
      asInteger(asJ(unify(l, k)).bar(unify(l, k)));
      asInteger(unify(l, k).bar(unify(l, k)));
      Object i2 = unify(l, k).foo(l, k);
      I _1 = unify(l, k).foo(l, k);
      J _2 = unify(l, k).foo(l, k);
      // choose(l, k).j();
      // check(choose(l,k));
    }
    abstract I asI(I i);
    abstract J asJ(J j);
    abstract Integer asInteger(Integer i);
  }
}

/* ///**************************************************************************
 * // ****************************** EXAMPLE 2 *********************************
 * // *************************************************************************/
class Example2 {
  interface X1 {
  }

  interface X2 {
  }

  interface X3 {
  }

  interface X4 {
  }

  interface Y extends X1, X2 {
  }

  interface Z extends X3, X4 {
  }

  interface X extends Y, Z {
  }

  abstract class Test2 {
    void test(X x) {
      check(x);
    }
    abstract void check(X1 x);
    abstract void check(X2 x);
  }
}

/// **************************************************************************
// ****************************** EXAMPLE 3 *********************************
// *************************************************************************/
abstract class Example3 {
  interface P<L, R> {
  }

  interface Left<L, R, RR> extends P<P<L, R>, RR> {
  }

  interface Right<L, R, LL> extends P<LL, P<L, R>> {
  }

  abstract <A> A unify(A a1, A a2);
  abstract <L, R, X extends P<P<L, L>, R>, Y extends P<L, P<R, R>>> X foo(X a, Y y);
  <L, R> void bar() {
    P<P<Integer, Integer>, String> _1 = foo(null, null);
    P<P<P<Object, Object>, P<Object, Object>>, Object> _2 = foo(null, foo(null, null));
    P<P<P<Integer, Integer>, P<Integer, Integer>>, String> _3 = foo(null, foo(null, null));
    P<P<P<L, L>, P<L, L>>, R> _4 = foo(null, foo(null, null));
  }
  abstract <A> A makeBoundedTypeVar(A a);
  abstract <A> A makeUnboundedTypeVar();
  abstract <A> A f(A a, A a2);
  abstract <A, B> P<A, B> g(A a, B b);

  class Sub1 extends Super {
  }

  class Sub2 extends Super {
  }

  class Super {
  }

  <L, R> void wikipedia() {
    String _1 = unify(makeUnboundedTypeVar(), ""); // inferred
    P<? extends Object, ? extends Object> _2 = unify(g("", 5), g(null, null)); // inferred
    AbstractList<Object> _3 = unify(makeBoundedTypeVar(new Stack<>()), makeBoundedTypeVar(new ArrayList<>())); // inferred
    String _4 = unify(makeBoundedTypeVar(makeUnboundedTypeVar()), makeUnboundedTypeVar()); // manual
    P<String, Integer> _5 = unify(makeBoundedTypeVar(g("", 5)), makeUnboundedTypeVar()); // inferred
    String choose = unify(f(makeUnboundedTypeVar(), null), f(null, ""));
  }
}

interface I1 {
  <T> T i1(T a, T b);
}

interface J1 {
  <T> T j1(T a, T b);
}

interface L1 extends I1, J1 {
}

interface K1 extends I1, J1 {
}
