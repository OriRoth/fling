package il.ac.technion.cs.fling.internal.compiler.api.dom;

import java.util.List;
import java.util.stream.Stream;

import il.ac.technion.cs.fling.internal.grammar.rules.Token;

/** Method node declaration.
 *
 * @author Ori Roth */
public class MethodDeclaration {
  /** Inducing token. */
  public final Token name;
  /** Inferred token parameters. Pending computation. */
  private List<ParameterFragment> parameters;

  public MethodDeclaration(final Token name) {
    this.name = name;
  }

  /** @return inferred parameters
   * @throws IllegalStateException whether the parameters have not been set */
  public List<ParameterFragment> getInferredParameters() {
    if (parameters == null)
      throw new IllegalStateException("parameter types and names not decided");
    return parameters;
  }

  /** Set token's inferred parameters.
   *
   * @param parameters parameters */
  public void setInferredParameters(final List<ParameterFragment> parameters) {
    this.parameters = parameters;
  }

  public Stream<ParameterFragment> parmeters() {
    return getInferredParameters().stream();
  }
}