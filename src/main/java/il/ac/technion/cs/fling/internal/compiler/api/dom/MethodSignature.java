package il.ac.technion.cs.fling.internal.compiler.api.dom;
import java.util.List;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
/** Models a signature of a method, including name, parameters and their names
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public class MethodSignature {
  /** Inducing token. */
  public final Token name;
  @Override public String toString() {
    return "MethodSignature [name=" + name + ", parameters=" + parameters + "]";
  }
  /** Inferred token parameters. Pending computation. */
  private List<MethodParameter> parameters;
  public MethodSignature(final Token name) {
    this.name = name;
  }
  /** @return inferred parameters
   * @throws IllegalStateException whether the parameters have not been set */
  public List<MethodParameter> getInferredParameters() {
    if (parameters == null)
      throw new IllegalStateException("parameter types and names not decided");
    return parameters;
  }
  /** Set token's inferred parameters.
   *
   * @param parameters parameters */
  public void setInferredParameters(final List<MethodParameter> parameters) {
    this.parameters = parameters;
  }
  public Stream<MethodParameter> parameters() {
    return getInferredParameters().stream();
  }
}