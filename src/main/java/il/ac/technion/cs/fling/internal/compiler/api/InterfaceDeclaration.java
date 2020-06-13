package il.ac.technion.cs.fling.internal.compiler.api;

import java.util.LinkedHashSet;
import java.util.Set;

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
  // TODO remove duplicate field.
  public final Word<Named> typeVariables;
  public final boolean isAccepting;

  public InterfaceDeclaration(final Named q, final Word<Named> α, final Set<Named> legalJumps,
      final Word<Named> typeVariables, final boolean isAccepting) {
    this.q = q;
    this.α = α;
    this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
    this.typeVariables = typeVariables;
    this.isAccepting = isAccepting;
  }

  InterfaceDeclaration() {
    q = null;
    α = null;
    legalJumps = null;
    typeVariables = null;
    isAccepting = false;
  }
}