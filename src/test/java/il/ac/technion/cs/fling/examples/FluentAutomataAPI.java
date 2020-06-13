package il.ac.technion.cs.fling.examples;

import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;

public interface FluentAutomataAPI<Σ extends Enum<Σ> & Terminal, V extends Enum<V> & Variable> {
  default String name() {
    return getClass().getSimpleName();
  }

  EBNF BNF();

  // TODO consider getting enums via reflection
  Class<Σ> Σ();

  Class<V> V();
}
