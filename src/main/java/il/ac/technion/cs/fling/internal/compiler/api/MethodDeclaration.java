package il.ac.technion.cs.fling.internal.compiler.api;

import java.util.List;

import il.ac.technion.cs.fling.internal.grammar.rules.Token;

/** Method node declaration.
 *
 * @author Ori Roth */
public class MethodDeclaration {
  /** Inducing token. */
  public final Token name;
  /** Inferred token parameters. Pending computation. */
  private List<ParameterFragment> inferredParameters;

  public MethodDeclaration(final Token name) {
    this.name = name;
  }

  /** @return inferred parameters
   * @throws IllegalStateException whether the parameters have not been set */
  public List<ParameterFragment> getInferredParameters() {
    if (inferredParameters == null)
      throw new IllegalStateException("parameter types and names not decided");
    return inferredParameters;
  }

  /** Set token's inferred parameters.
   *
   * @param inferredParameters parameters */
  public void setInferredParameters(final List<ParameterFragment> inferredParameters) {
    this.inferredParameters = inferredParameters;
  }
}