package il.ac.technion.cs.fling.examples;
import il.ac.technion.cs.fling.EBNF;
import il.ac.technion.cs.fling.internal.grammar.rules.Quantifiers;
import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;
import il.ac.technion.cs.fling.internal.grammar.rules.Variable;
public interface FluentLanguageAPI<Σ extends Enum<Σ> & Terminal, Γ extends Enum<Γ> & Variable> extends Quantifiers {
  default String name() {
    return getClass().getSimpleName();
  }
  EBNF BNF();
  // TODO consider getting enums via reflection
  Class<Σ> Σ();
  Class<Γ> Γ();
}
