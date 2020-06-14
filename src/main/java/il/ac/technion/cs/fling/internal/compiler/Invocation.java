package il.ac.technion.cs.fling.internal.compiler;

import java.util.Arrays;
import java.util.List;

import il.ac.technion.cs.fling.internal.grammar.rules.Terminal;

/** Recording of method call in fluent API invocation.
 *
 * @author Ori Roth */
public class Invocation {
  /** Inducing terminal. */
  public final Terminal σ;
  /** Arguments supplied. */
  public final List<Object> arguments;

  public Invocation(final Terminal σ, final Object... arguments) {
    this.σ = σ;
    this.arguments = Arrays.asList(arguments);
  }
}
