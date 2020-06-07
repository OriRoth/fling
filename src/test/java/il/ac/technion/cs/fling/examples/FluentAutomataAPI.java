package il.ac.technion.cs.fling.examples;

import il.ac.technion.cs.fling.*;

public interface FluentAutomataAPI<Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> {
  default String name() {
    return this.getClass().getSimpleName();
  }
  int a𝟐２=2;
  int ש ２=3;
  int a١2=1;
  BNF BNF();
  // TODO consider getting enums via reflection
  Class<Σ> Σ();
  Class<V> V();
}
