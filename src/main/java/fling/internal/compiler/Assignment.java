package fling.internal.compiler;

import java.util.Arrays;
import java.util.List;

import fling.internal.grammar.sententials.Terminal;

/**
 * Recording of method call in fluent API invocation.
 * 
 * @author Ori Roth
 */
public class Assignment {
  /**
   * Inducing terminal.
   */
  public final Terminal σ;
  /**
   * Arguments supplied.
   */
  public final List<Object> arguments;

  public Assignment(Terminal σ, Object... arguments) {
    this.σ = σ;
    this.arguments = Arrays.asList(arguments);
  }
}
