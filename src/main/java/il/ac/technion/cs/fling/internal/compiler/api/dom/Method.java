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
    private final Token name;
    NamedMethod(final Token name) {
      this.name = name;
    }
    public Method returning(final Type.Grounded returnType) {
      return new Method(name, returnType);
    }
  }
  public static NamedMethod named(final Token name) {
    return new NamedMethod(name);
  }
  /** Inducing token. */
  public final Token name;
  /** Inferred token parameters. Pending computation. */
  private List<MethodParameter> parameters = null;
  /** Return type of this method */
  public final Type.Grounded type;
  /** @param name
   * @param type */
  private Method(final Token name, final Type.Grounded type) {
    this.name = name;
    this.type = type;
  }
  /** Set token's inferred parameters.
   *
   * @param parameters parameters */
  public void populateParameters(@SuppressWarnings("hiding") final List<MethodParameter> parameters) {
    this.parameters = parameters;
  }
  /** @return inferred parameters
   * @throws IllegalStateException whether the parameters have not been set */
  public Stream<MethodParameter> parameters() {
    if (parameters == null)
      throw new IllegalStateException("parameter types and names not decided");
    return parameters.stream();
  }
  public static Method termination() {
    return named(Constants.$$).returning(Type.Grounded.TOP);
  }
}
