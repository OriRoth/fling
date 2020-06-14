package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Type name node declaration.
 *
 * @author Ori Roth */
public class TypeName extends SimpleTypeName {
  /** Inducing stack symbols. */
  public final Word<Named> α;
  /** Referenced states (type variables). */
  public final Set<Named> legalJumps;

  public TypeName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
    super(q);
    this.α = α;
    this.legalJumps = legalJumps == null ? null : new LinkedHashSet<>(legalJumps);
  }

  public TypeName(final Named q) {
    this(q, null, null);
  }

  @Override public int hashCode() {
    int $ = 1;
    if (q != null)
      $ = $ * 31 + q.hashCode();
    if (α != null)
      $ = $ * 31 + α.hashCode();
    if (legalJumps != null)
      $ = $ * 31 + legalJumps.hashCode();
    return $;
  }

  @Override public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TypeName))
      return false;
    final TypeName other = (TypeName) o;
    return Objects.equals(q, other.q) && //
        Objects.equals(α, other.α) && //
        Objects.equals(legalJumps, other.legalJumps);
  }

  @Override public String toString() {
    return String.format("<~%s,%s,%s~>", q, α, legalJumps);
  }
}