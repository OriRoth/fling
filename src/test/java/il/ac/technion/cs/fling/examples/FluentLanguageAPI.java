package il.ac.technion.cs.fling.examples;

import il.ac.technion.cs.fling.*;

public interface FluentLanguageAPI<Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> {
  default String name() {
    return this.getClass().getSimpleName();
  }

  FancyEBNF BNF();

  // TODO consider getting enums via reflection
  Class<Σ> Σ();

  Class<V> V();
}
