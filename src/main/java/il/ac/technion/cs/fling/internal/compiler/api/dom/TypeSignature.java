package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

import il.ac.technion.cs.fling.internal.grammar.rules.Named;
import il.ac.technion.cs.fling.internal.grammar.rules.Word;

/** Models the signature of a type in the fluent API model, including components
 * of the type name and the formal parameters
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public class TypeSignature {
  /** Inducing state. */
  public final Named q;
  /** Inducing stack symbols. */
  public final @NonNull Word<Named> α;
  /** Referenced states (type variables). */
  public final Set<Named> legalJumps;
  /** Referenced states (type variables). */
  public final Word<Named> parameters;
  public final boolean isAccepting;

  public TypeSignature(final Named q, final Word<Named> α, final Set<Named> legalJumps, final Word<Named> parameters,
      final boolean isAccepting) {
    Objects.requireNonNull(q);
    Objects.requireNonNull(α);
    Objects.requireNonNull(parameters);
    this.q = q;
    this.α = α;
    this.legalJumps = legalJumps;
    this.parameters = parameters;
    this.isAccepting = isAccepting;
  }

  public Stream<Named> parameters() {
    return parameters.stream();
  }

}