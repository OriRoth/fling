package automaton.seminar;

class EmptyStack implements AbstractStack<EmptyStack> {
  Stack<EmptyStack> push(){return null;}
}
@SuppressWarnings({"all"})
interface AbstractStack<Tail extends AbstractStack<?>> {
}

public interface Stack<Tail extends AbstractStack<?> > 
                  extends AbstractStack<Tail> {
  Stack<Stack<Tail>> push();
  Tail pop();

  public static void main(String[] args) {
    EmptyStack e = new EmptyStack();
    Stack<EmptyStack> p1 = e.push();
    Stack<Stack<EmptyStack>> p2 = p1.push();
    EmptyStack pop = p2.pop().pop();
//    p2.pop().pop().pop();  // shouldn't compile
  }
}
