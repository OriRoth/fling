package il.ac.technion.cs.fling.internal.compiler.api.dom;
import java.util.*;
import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;
/** Type name node declaration.
 *
 * @author Ori Roth */
class TypeName extends SimpleTypeName {
  /** Inducing stack symbols. */
  private final Word<Named> α;
  /** Referenced states (type variables). */
  private final Set<Named> legalJumps;
  private TypeName(final Named q, final Word<Named> α, final Set<Named> legalJumps) {
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
    if (!(o instanceof TypeName other))
      return false;
    return Objects.equals(q, other.q) && //
        Objects.equals(α, other.α) && //
        Objects.equals(legalJumps, other.legalJumps);
  }
  @Override public String toString() {
    return String.format("<~%s,%s,%s~>", q, α, legalJumps);
  }
}