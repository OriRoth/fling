package automaton.stack;

interface EmptyStack extends AbstractStack<EmptyStack> {
  Stack<EmptyStack> push();
}
@SuppressWarnings({"all"})
interface AbstractStack<Tail extends AbstractStack<?>> {
}

public interface Stack<Tail extends AbstractStack<?> > 
                  extends AbstractStack<Tail> {
  Stack<Stack<Tail>> push();
  Tail pop();

  @SuppressWarnings("cast")
  public static void main(String[] args) {
    EmptyStack e = (EmptyStack)null;
    Stack<EmptyStack> p1 = e.push();
    Stack<Stack<EmptyStack>> p2 = p1.push();
    EmptyStack pop = p2.pop().pop();
    p2.pop().pop().pop();
  }
}
