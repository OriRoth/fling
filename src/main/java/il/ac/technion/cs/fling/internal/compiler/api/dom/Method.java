package il.ac.technion.cs.fling.internal.compiler.api.dom;
import java.util.List;
import java.util.stream.Stream;
import il.ac.technion.cs.fling.internal.grammar.rules.Constants;
import il.ac.technion.cs.fling.internal.grammar.rules.Token;
/** Models methods in the fluent API model
 * 
 * @author Yossi Gil
 *
 * @since 2020-06-15 */
public class Method {
  public static class NamedMethod {
    private Token name;
    NamedMethod(Token name) {
      this.name = name;
    }
    public Method returning(Type.Instantiation returnType) {
      return new Method(name, returnType);
    }
  }
  public static NamedMethod named(Token name) {
    return new NamedMethod(name);
  }
  /** Inducing token. */
  public final Token name;
  /** Inferred token parameters. Pending computation. */
  private List<MethodParameter> parameters = null;
  /** Return type of this method */
  public final Type.Instantiation type;
  /** @param name
   * @param type */
  private Method(Token name, Type.Instantiation type) {
    this.name = name;
    this.type = type;
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
  public void populateParameters(final List<MethodParameter> parameters) {
    this.parameters = parameters;
  }
  public Stream<MethodParameter> parameters() {
    return getInferredParameters().stream();
  }
  public static Method termination() {
    return named(Constants.$$).returning(Type.Instantiation.TOP);
  }
}
