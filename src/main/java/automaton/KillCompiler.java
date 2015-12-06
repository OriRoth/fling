package automaton;

public class KillCompiler {
  interface Cons<Car, Cdr> {
    Cons< Cons<Car, Cdr>, Cons<Car, Cdr> > d();
  }
}
