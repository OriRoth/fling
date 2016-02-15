package automaton.stack;

public abstract class StateStack<Tail extends IStack<?>> implements IStack<Tail> {
  public final Tail t;

  public StateStack(final Tail t) {
    this.t = t;
  }
}
