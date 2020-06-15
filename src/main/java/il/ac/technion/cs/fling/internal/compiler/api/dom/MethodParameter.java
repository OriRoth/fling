package il.ac.technion.cs.fling.internal.compiler.api.dom;

/** Parameter declaration inferred from token. Single token may define multiple
 * parameters.
 *
 * @author Ori Roth */
public class MethodParameter {
  /** Parameter type name. */
  public final String parameterType;
  /** Parameter variable name. */
  public final String parameterName;

  private MethodParameter(final String parameterType, final String parameterName) {
    this.parameterType = parameterType;
    this.parameterName = parameterName;
  }

  public static MethodParameter of(final String parameterType, final String parameterName) {
    return new MethodParameter(parameterType, parameterName);
  }
}