package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Type node declaration.
 *
 * @author Ori Roth */
public class InterfaceDeclaration {
  /** Inducing state. */
  public final Named q;
  /** Inducing stack symbols. */
  public final Word<Named> α;
  /** Referenced states (type variables). */
  public final Set<Named> legalJumps;
  /** Referenced states (type variables). */
  public final Word<Named> parameters;
  public final boolean isAccepting;

  public InterfaceDeclaration(final Named q, final Word<Named> α, final Set<Named> legalJumps,
      final Word<Named> parameters, final boolean isAccepting) {
    this.q = q;
    this.α = α;
    this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
    this.parameters = parameters;
    this.isAccepting = isAccepting;
  }

  public Stream<Named> parameters() {
    return parameters.stream();
  }

}