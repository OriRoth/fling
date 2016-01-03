package automaton;

public class Prolog {
  static <X1,Y1, Z1, X extends P<X1, P<Y1, Z1>> > X f(X x) {return x;}
  static <X2,Y2, Z2, X extends P<P<Y2, Z2>,X2> > X g(X x) { return x;}
  public static void main(String[] args) {
    P<P<String, Integer>,? extends P> x =null;
 P<P<String, Integer>, ? extends Object> g = g(x);
    f(x);  
  }
}

class P<X,Y> {}
