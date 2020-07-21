package il.ac.technion.cs.fling.internal.compiler.api._;
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
class TypeSignature {
  /** Inducing stack symbols. */
  private final @NonNull Word<Named> α;
  /** Referenced states (type variables). */
  private final Word<Named> parameters;
  public TypeSignature(final Named q, final Word<Named> α, final Set<Named> legalJumps, final Word<Named> parameters,
      final boolean isAccepting) {
    Objects.requireNonNull(q);
    Objects.requireNonNull(α);
    Objects.requireNonNull(parameters);
    this.α = α;
    this.parameters = parameters;
  }
  public Stream<Named> parameters() {
    return parameters.stream();
  }
}