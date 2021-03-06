package il.ac.technion.cs.fling.internal.compiler.api.dom;
/** Parameter declaration inferred from token. Single token may define multiple
 * parameters.
 *
 * @author Ori Roth */
public class MethodParameter {
  /** Parameter type name. */
  public final String type;
  /** Parameter variable name. */
  public final String name;
  private MethodParameter(final String parameterType, final String parameterName) {
    this.type = parameterType;
    this.name = parameterName;
  }
  public static MethodParameter of(final String parameterType, final String parameterName) {
    return new MethodParameter(parameterType, parameterName);
  }
}